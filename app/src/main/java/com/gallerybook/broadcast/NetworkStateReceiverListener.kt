package com.gallerybook.broadcast

/**
 * Created by Nihal Srivastava on 30/10/21.
 */
interface NetworkStateReceiverListener {
    fun networkAvailable()
    fun networkUnavailable()
}