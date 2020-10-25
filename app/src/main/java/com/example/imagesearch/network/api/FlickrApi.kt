package com.example.imagesearch.network.api

import com.example.imagesearch.datas.FlickrResponseData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

//https://www.flickr.com/services/rest/?method=flickr.photos.search&api_key=27e3daad3363a23e65f32619a3231aef&text=cats&per_page=10&page=20&format=json&nojsoncallback=1

interface FlickrApi {

    companion object{
        // String to create Flickr API urls
        const val FLICKR_BASE_URL = "https://www.flickr.com/services/rest/"
    }


    @GET("?method=flickr.photos.search&api_key=27e3daad3363a23e65f32619a3231aef&format=json&nojsoncallback=1")
    suspend fun searchPhotos2(
        @Query("text") text :String,
        @Query("page") page : Int,
        @Query("per_page") per_page : Int
    )  : FlickrResponseData


}