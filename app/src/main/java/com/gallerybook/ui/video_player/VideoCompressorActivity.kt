package com.gallerybook.ui.video_player

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.gallerybook.BaseActivity
import com.gallerybook.R
import com.gallerybook.compression.CompressionListener
import com.gallerybook.compression.VideoCompressor
import com.gallerybook.compression.VideoQuality
import com.gallerybook.compression.config.Configuration
import com.gallerybook.utils.GalleryUtils
import com.gallerybook.utils.getFileSize
import com.gallerybook.utils.getMediaPath
import kotlinx.android.synthetic.main.activity_video_compress.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class VideoCompressorActivity : BaseActivity() {

    companion object {
        const val REQUEST_SELECT_VIDEO = 0
        const val REQUEST_CAPTURE_VIDEO = 1
    }

    private lateinit var playableVideoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_compress)
        cancel.visibility = View.GONE
        setReadStoragePermission()
        pickVideo.setOnClickListener {
            pickVideo()
        }
        recordVideo.setOnClickListener {
            dispatchTakeVideoIntent()
        }
        cancel.setOnClickListener {
            VideoCompressor.cancel()
        }
        iv_toolbar_menu.setOnClickListener {
            onBackPressed()
        }
        videoLayout.setOnClickListener { VideoPlayerActivity.start(this, playableVideoPath) }
    }

    //Pick a video file from device
    private fun pickVideo() {
        val intent = Intent()
        intent.apply {
            type = "video/*"
            action = Intent.ACTION_PICK
        }
        startActivityForResult(Intent.createChooser(intent, "Select video"), REQUEST_SELECT_VIDEO)
    }

    private fun dispatchTakeVideoIntent() {
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            takeVideoIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takeVideoIntent, REQUEST_CAPTURE_VIDEO)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        mainContents.visibility = View.GONE
        timeTaken.text = ""
        newSize.text = ""

        if (resultCode == Activity.RESULT_OK)
            if (requestCode == REQUEST_SELECT_VIDEO || requestCode == REQUEST_CAPTURE_VIDEO) {
                handleResult(data)
            }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleResult(data: Intent?) {
        if (data != null && data.data != null) {
            val uri = data.data

            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor = contentResolver.query(uri!!, filePathColumn, null, null, null)!!
            cursor.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
            val filePath: String = cursor.getString(columnIndex)
            playableVideoPath = filePath
            cursor.close()
            val file = File(filePath)
            val file_size = (file.length() / 1024).toString().toInt()
            val fileSizeInMB = file_size / 1024
            if (fileSizeInMB > 10) {
                processVideo(uri)
            } else {
                GalleryUtils.showToast(this, "Size is less than 10 mb")
            }
        }
    }

    private fun setReadStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun processVideo(uri: Uri?) {
        GalleryUtils.showLoader(this)
        var streamableFile: File? = null
        uri?.let {
            mainContents.visibility = View.VISIBLE
            Glide.with(applicationContext).load(uri).into(videoImage)

            CoroutineScope(Dispatchers.IO).launch {
                val job = async { getMediaPath(applicationContext, uri) }
                val path = job.await()

                val desFile = saveVideoFile(path)
                streamableFile = saveVideoFile(path)

                playableVideoPath = if (streamableFile != null) streamableFile!!.path
                else path

                desFile?.let {
                    var time = 0L
                    VideoCompressor.start(
                        context = applicationContext,
                        srcUri = uri,
                        // srcPath = path,
                        destPath = desFile.path,
                        streamableFile = streamableFile?.path,
                        listener = object : CompressionListener {
                            override fun onProgress(percent: Float) {
                                if (percent <= 100 && percent.toInt() % 5 == 0)
                                    runOnUiThread {
                                        progress.text = "${percent.toLong()}%"
                                        progressBar.progress = percent.toInt()
                                    }
                            }

                            override fun onStart() {
                                time = System.currentTimeMillis()
                                progress.visibility = View.VISIBLE
                                cancel.visibility = View.VISIBLE
                                GalleryUtils.hideLoader()
                                progressBar.visibility = View.VISIBLE
                                originalSize.text = "Original size: ${getFileSize(File(path).length())}"
                                progress.text = ""
                                progressBar.progress = 0
                            }

                            override fun onSuccess() {
                                val newSizeValue =
                                    if (streamableFile != null) streamableFile!!.length()
                                    else desFile.length()

                                newSize.text = "Size after compression: ${getFileSize(newSizeValue)}"

                                time = System.currentTimeMillis() - time
                                timeTaken.text = "Duration: ${DateUtils.formatElapsedTime(time / 1000)}"
                                cancel.visibility = View.GONE
                                GalleryUtils.showToast(this@VideoCompressorActivity,"Compressed Video saved in Gallery !!")
                                Looper.myLooper()?.let {
                                    Handler(it).postDelayed({
                                        progress.visibility = View.GONE
                                        progressBar.visibility = View.GONE
                                    }, 50)
                                }
                            }

                            override fun onFailure(failureMessage: String) {
                                progress.text = failureMessage
                                cancel.visibility = View.GONE
                            }

                            override fun onCancelled() {
                                this@VideoCompressorActivity.finish()
                            }
                        },
                        configureWith = Configuration(
                            quality = VideoQuality.HIGH,
                            frameRate = 24,
                            isMinBitrateCheckEnabled = true,
                        )
                    )
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun saveVideoFile(filePath: String?): File? {
        filePath?.let {
            val videoFile = File(filePath)
            val videoFileName = "${System.currentTimeMillis()}_${videoFile.name}"
            val folderName = Environment.DIRECTORY_MOVIES
            if (Build.VERSION.SDK_INT >= 30) {

                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, videoFileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "video/mp4")
                    put(MediaStore.Images.Media.RELATIVE_PATH, folderName)
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                val collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

                val fileUri = applicationContext.contentResolver.insert(collection, values)

                fileUri?.let {
                    application.contentResolver.openFileDescriptor(fileUri, "rw")
                        .use { descriptor ->
                            descriptor?.let {
                                FileOutputStream(descriptor.fileDescriptor).use { out ->
                                    FileInputStream(videoFile).use { inputStream ->
                                        val buf = ByteArray(4096)
                                        while (true) {
                                            val sz = inputStream.read(buf)
                                            if (sz <= 0) break
                                            out.write(buf, 0, sz)
                                        }
                                    }
                                }
                            }
                        }

                    values.clear()
                    values.put(MediaStore.Video.Media.IS_PENDING, 0)
                    applicationContext.contentResolver.update(fileUri, values, null, null)
                    return File(getMediaPath(applicationContext, fileUri))
                }
            } else {
                val downloadsPath =
                    applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                val desFile = File(downloadsPath, videoFileName)

                if (desFile.exists())
                    desFile.delete()
                try {
                    desFile.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                return desFile
            }
        }
        return null
    }
}
