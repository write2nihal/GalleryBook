package com.gallerybook.model

import com.gallerybook.room.GalleryScope

/**
 * Created by Nihal Srivastava on 30/10/21.
 */
data class ResponseApi(
    val results: List<GalleryScope>
)