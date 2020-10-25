package com.example.imagesearch.network


import androidx.paging.PagingSource
import com.example.imagesearch.datas.PhotoInfo
import com.example.imagesearch.network.api.FlickrApi
import retrofit2.HttpException
import java.io.IOException

private const val FLICKR_STARTING_PAGE_NUMBER = 1

class FlickrPagingSource(
    private val flickrApi: FlickrApi,
    private val query : String
) : PagingSource<Int, PhotoInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoInfo> {
        val position = params.key?: FLICKR_STARTING_PAGE_NUMBER

        return try {

            val respose  = flickrApi.searchPhotos2(
                text = query,
                page = position,
                per_page = params.loadSize
            )
            val photos  = respose.photoes.list

            LoadResult.Page(
                data = photos,
                prevKey = if(position== FLICKR_STARTING_PAGE_NUMBER) null else position -1,
                nextKey = if(photos.isEmpty()) null else position+1,
            )
        }catch (exception : IOException){
            // when there is no internet connection while loading the photos
            LoadResult.Error(exception)
        }catch (exception : HttpException){
            // when the sever doesn't contains any result for search term
            // or not authorized to make result.
            LoadResult.Error(exception)
        }
    }
}