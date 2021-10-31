package com.gallerybook.room

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Nihal Srivastava on 30/10/21.
 */


@Entity(tableName = "gallery_album")
data class GalleryScope(

    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val user_name: String,
    val email: String,
    val password: String,
    val mobile: String,
    val image_path: String,
    val description: String,
    val download_date : String,

    val image: String,
    val name: String,
    val status: String,
    val url: String
)