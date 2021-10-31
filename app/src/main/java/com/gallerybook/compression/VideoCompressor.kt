package com.gallerybook.compression

import android.content.Context
import android.net.Uri
import com.gallerybook.compression.compressor.Compressor.compressVideo
import com.gallerybook.compression.compressor.Compressor.isRunning
import com.gallerybook.compression.config.Configuration
import com.gallerybook.compression.video.Result
import kotlinx.coroutines.*

/**
 * Created by Nihal Srivastava on 31/10/21.
 */

enum class VideoQuality {
    VERY_HIGH, HIGH, MEDIUM, LOW, VERY_LOW
}

object VideoCompressor : CoroutineScope by MainScope() {

    private var job: Job? = null

    @JvmStatic
    @JvmOverloads
    fun start(
        context: Context? = null,
        srcUri: Uri? = null,
        srcPath: String? = null,
        destPath: String,
        streamableFile: String? = null,
        listener: CompressionListener,
        configureWith: Configuration,
    ) {
        job = doVideoCompression(
            context,
            srcUri,
            srcPath,
            destPath,
            streamableFile,
            configureWith,
            listener,
        )
    }

    @JvmStatic
    fun cancel() {
        job?.cancel()
        isRunning = false
    }

    private fun doVideoCompression(
        context: Context?,
        srcUri: Uri?,
        srcPath: String?,
        destPath: String,
        streamableFile: String? = null,
        configuration: Configuration,
        listener: CompressionListener,
    ) = launch {
        isRunning = true
        listener.onStart()
        val result = startCompression(
            context,
            srcUri,
            srcPath,
            destPath,
            streamableFile,
            configuration,
            listener,
        )

        // Runs in Main(UI) Thread
        if (result.success) {
            listener.onSuccess()
        } else {
            listener.onFailure(result.failureMessage ?: "An error has occurred!")
        }

    }

    private suspend fun startCompression(
        context: Context?,
        srcUri: Uri?,
        srcPath: String?,
        destPath: String,
        streamableFile: String? = null,
        configuration: Configuration,
        listener: CompressionListener,
    ): Result = withContext(Dispatchers.IO) {
        return@withContext compressVideo(
            context,
            srcUri,
            srcPath,
            destPath,
            streamableFile,
            configuration,
            object : CompressionProgressListener {
                override fun onProgressChanged(percent: Float) {
                    listener.onProgress(percent)
                }

                override fun onProgressCancelled() {
                    listener.onCancelled()
                }
            },
        )
    }
}
