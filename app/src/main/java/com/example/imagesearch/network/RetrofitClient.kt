package com.example.imagesearch.network

import android.util.Log
import com.example.imagesearch.network.api.FlickrApi
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    private val TAG = "RetrofitClient"
    private var retrofit :Retrofit? = null;
    private val gson =  GsonBuilder().setLenient().create()

    val client : Retrofit
        get() {
            if(retrofit == null){
                synchronized(Retrofit::class.java) {
                    if(retrofit == null){
                        Log.d(TAG, " Initialize Retrofit : called ")
                        retrofit = Retrofit.Builder()
                            .baseUrl(FlickrApi.FLICKR_BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build()
                    }
                }
            }

            return retrofit!!
        }
}