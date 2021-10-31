package com.gallerybook.ui.details

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.gallerybook.BaseActivity
import com.gallerybook.R
import com.gallerybook.databinding.ActivityDetailsBinding
import com.gallerybook.room.GalleryDatabase
import com.gallerybook.room.GalleryScope
import com.gallerybook.ui.splash.SplashActivity
import com.gallerybook.utils.Constants
import com.gallerybook.utils.GalleryUtils
import com.gallerybook.utils.PopUpManager
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


/**
 * Created by Nihal Srivastava on 30/10/21.
 */
class ProductDetailsActivity : BaseActivity(), View.OnClickListener {


    private lateinit var data: GalleryScope
    private lateinit var dashBoardBinding: ActivityDetailsBinding
    private lateinit var database: GalleryDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashBoardBinding = DataBindingUtil.setContentView(this, R.layout.activity_details)
        iv_toolbar_menu.visibility = View.VISIBLE
        iv_popup_menu.visibility = View.GONE
        iv_toolbar_menu.setImageResource(R.drawable.backarrow)
        database = GalleryDatabase.initACDatabase(this)
        val extras = intent.extras
        if (!GalleryUtils.hasInternet(this)) {
            dashBoardBinding.btnSaveImage.visibility = View.GONE
        }
        if (extras != null) {
            val value = extras.getString(Constants.MODEL_VALUE)
            val gson = GsonBuilder().create()
            data = gson.fromJson(value, GalleryScope::class.java)
            var imageLink = ""
            if (data.image.startsWith("http")) {
                imageLink = data.image
                tv_title_toolbar.text = data.name
            } else if (data.image_path.startsWith("/storage/")) {
                val file = File(data.image_path)
                val imageUri: Uri = Uri.fromFile(file)
                imageLink = imageUri.toString()
                tv_title_toolbar.text = data.description
            }
            Glide.with(dashBoardBinding.ivImage).load(imageLink).into(dashBoardBinding.ivImage)

            iv_toolbar_menu.setOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dashBoardBinding.btnSaveImage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_save_image -> {
                disableUpdate()
                GalleryUtils.showLoader(this)
                Glide.with(this@ProductDetailsActivity)
                    .asBitmap()
                    .load(data.image)
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onLoadCleared(placeholder: Drawable?) {}
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            saveImage(resource)
                            GalleryUtils.hideLoader()
                            GalleryUtils.showToast(
                                this@ProductDetailsActivity,
                                "Image saved successfully !!"
                            )
                        }
                    })
            }
        }
    }


    private fun saveImage(image: Bitmap): String? {
        var savedImagePath: String? = null
        val imageFileName = "JPEG_" + System.currentTimeMillis() + ".jpg"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/GalleryBook" +"/Images"
        )
        var success = true
        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }
        if (success) {
            val imageFile = File(storageDir, imageFileName)
            savedImagePath = imageFile.absolutePath
            try {
                val fOut: OutputStream = FileOutputStream(imageFile)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()
                CoroutineScope(Dispatchers.IO).launch {
                    database.galleryDao().insertOperation(
                        GalleryScope(
                            0,
                            "",
                            "",
                            "",
                            "",
                            savedImagePath,
                            data.name,
                            GalleryUtils.getFormattedDate(System.currentTimeMillis()),
                            "",
                            "",
                            "",
                            ""
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            galleryAddPic(savedImagePath)
        }
        return savedImagePath
    }

    private fun galleryAddPic(imagePath: String?) {
        imagePath?.let { path ->
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val f = File(path)
            val contentUri: Uri = Uri.fromFile(f)
            mediaScanIntent.data = contentUri
            sendBroadcast(mediaScanIntent)
        }
    }

    private fun disableUpdate() {
        dashBoardBinding.btnSaveImage.isEnabled = false
        val sdk: Int = android.os.Build.VERSION.SDK_INT
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            dashBoardBinding.btnSaveImage.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.btn_rounded_red_deactivated
                )
            )
        } else {
            dashBoardBinding.btnSaveImage.background =
                ContextCompat.getDrawable(this, R.drawable.btn_rounded_red_deactivated)
        }
    }

}