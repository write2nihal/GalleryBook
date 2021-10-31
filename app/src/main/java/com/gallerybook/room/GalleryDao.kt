package com.galleryalbum.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.gallerybook.room.GalleryScope

/**
 * Created by Nihal Srivastava on 30/10/21.
 */

@Dao
interface GalleryDao {
    @Insert
    suspend fun insertOperation(galleryScope: GalleryScope)

    @Update
    suspend fun updateOperation(galleryScope: GalleryScope)

    @Delete
    suspend fun deleteOperation(galleryScope: GalleryScope)

    @Query("SELECT * FROM gallery_album")
    fun fetchStoredACDetails(): LiveData<List<GalleryScope>>

    @Query("SELECT * FROM gallery_album WHERE email=:user_id AND password =:password")
    fun fetchStoredUserDetails(user_id: String, password: String): List<GalleryScope>

    @Query("SELECT * FROM gallery_album")
    fun isThereAnyData(): List<GalleryScope>

}