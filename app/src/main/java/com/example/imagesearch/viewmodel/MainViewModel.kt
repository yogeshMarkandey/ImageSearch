package com.example.imagesearch.viewmodel

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.imagesearch.datas.PhotoInfo
import com.example.imagesearch.datas.Photoes
import com.example.imagesearch.network.RetrofitClient
import com.example.imagesearch.network.api.FlickrApi
import com.example.imagesearch.repository.MainRepository


class MainViewModel : ViewModel() {



    val gridLayoutSpanCount = MutableLiveData<Int>()

    private val apiInterface: FlickrApi = RetrofitClient.client.create(FlickrApi::class.java)
    private val mainRepository = MainRepository(apiInterface)

    init {
        gridLayoutSpanCount.value = 2
    }

    public val currentQuery = MutableLiveData<String>(DEFAULT_QUERY)

    val photes = currentQuery.switchMap { queryString ->
        mainRepository.getSearchResult(queryString).cachedIn(viewModelScope)
    }

    fun searchPhotos(query: String) {
        currentQuery.value = query
    }

    companion object {
        public const val DEFAULT_QUERY = "Nature"
    }

    fun setSpanCount(count: Int) {
        if (count != gridLayoutSpanCount.value) gridLayoutSpanCount.postValue(count)
    }


    fun saveImageInfo(photoInfo: PhotoInfo) {
        mainRepository.savePhoto(photoInfo)
    }

}