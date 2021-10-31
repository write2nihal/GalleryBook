package com.gallerybook.di

import com.gallerybook.api.ApiService
import com.gallerybook.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by Nihal Srivastava on 30/10/21.
 */

@Module
@InstallIn(SingletonComponent::class)
object GalleryAppModule {

    @Provides
    fun fetchBaseUrl() = Constants.BASE_URL

    @Provides
    @Singleton
    fun retrofitInstance(BASE_URL: String): ApiService =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)


}