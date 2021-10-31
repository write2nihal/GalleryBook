package com.gallerybook.viewmodel

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.gallerybook.api.ApiService
import com.gallerybook.paging.GalleryPaging

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Nihal Srivastava on 30/10/21.
 */

@HiltViewModel
class GalleryViewModel
@Inject
constructor(
    private val apiService: ApiService
) : ViewModel() {

    val listData = Pager(PagingConfig(pageSize = 1)) {
        GalleryPaging(apiService)

    }.flow.cachedIn(viewModelScope)

}
























