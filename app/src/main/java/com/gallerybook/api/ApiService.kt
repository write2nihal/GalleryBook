package com.gallerybook.api


import com.gallerybook.model.ResponseApi
import com.gallerybook.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Nihal Srivastava on 30/10/21.
 */

interface ApiService {
    @GET(Constants.END_POINT)
    suspend fun fetchImages(
        @Query("page") page: Int

    ): Response<ResponseApi>
}