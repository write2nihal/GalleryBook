package com.gallerybook.compression.config

import com.gallerybook.compression.VideoQuality

/**
 * Created by Nihal Srivastava on 31/10/21.
 */
data class Configuration(
    var quality: VideoQuality = VideoQuality.MEDIUM,
    var frameRate: Int? = null,
    var isMinBitrateCheckEnabled: Boolean = true,
    var videoBitrate: Int? = null,
)
