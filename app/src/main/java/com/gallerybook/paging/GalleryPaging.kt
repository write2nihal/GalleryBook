package com.gallerybook.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gallerybook.room.GalleryScope
import com.gallerybook.api.ApiService

/**
 * Created by Nihal Srivastava on 30/10/21.
 */

class GalleryPaging(private val apiService: ApiService) : PagingSource<Int, GalleryScope>() {

    override fun getRefreshKey(state: PagingState<Int, GalleryScope>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>):
            LoadResult<Int, GalleryScope> {

        return try {
            val currentPage = params.key ?: 1
            val response = apiService.fetchImages(currentPage)
            val responseData = mutableListOf<GalleryScope>()
            val data = response.body()?.results ?: emptyList()
            responseData.addAll(data)

            LoadResult.Page(
                data = responseData,
                prevKey = if (currentPage == 1) null else -1,
                nextKey = currentPage.plus(1)
            )
        } catch (e: Exception) {
            Log.e("ERROR_DATA", "load: ${e.localizedMessage}", )
            LoadResult.Error(e)
        }

    }
}