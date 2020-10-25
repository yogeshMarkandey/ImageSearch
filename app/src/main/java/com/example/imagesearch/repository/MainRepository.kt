package com.example.imagesearch.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.imagesearch.datas.PhotoInfo
import com.example.imagesearch.network.FlickrPagingSource
import com.example.imagesearch.network.api.FlickrApi

class MainRepository(
    private val flickrApi: FlickrApi
) {

    fun getSearchResult(query : String) =
        Pager(
            config = PagingConfig(
                pageSize = 60,
                maxSize = 300,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {FlickrPagingSource(flickrApi, query)}

        ).liveData

    var imageInfo : PhotoInfo? = null

    fun savePhoto(photoInfo: PhotoInfo){
        imageInfo = photoInfo
    }
}