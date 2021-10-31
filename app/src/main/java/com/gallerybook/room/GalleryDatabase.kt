package com.gallerybook.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.galleryalbum.room.GalleryDao
import com.gallerybook.utils.Constants

/**
 * Created by Nihal Srivastava on 30/10/21.
 */

@Database(entities = [GalleryScope::class], version = 1)
abstract class GalleryDatabase : RoomDatabase() {

    abstract fun galleryDao(): GalleryDao

    companion object {
        @Volatile
        private var INSTANCE: GalleryDatabase? = null

        fun initACDatabase(context: Context): GalleryDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        GalleryDatabase::class.java,
                        Constants.DB_NAME
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}