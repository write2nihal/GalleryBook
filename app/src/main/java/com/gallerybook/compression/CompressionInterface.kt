package com.gallerybook.compression

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread

/**
 * Created by Nihal Srivastava on 31/10/21.
 */
interface CompressionListener {
    @MainThread
    fun onStart()

    @MainThread
    fun onSuccess()

    @MainThread
    fun onFailure(failureMessage: String)

    @WorkerThread
    fun onProgress(percent: Float)

    @WorkerThread
    fun onCancelled()
}

interface CompressionProgressListener {
    fun onProgressChanged(percent: Float)
    fun onProgressCancelled()
}
