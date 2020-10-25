package com.example.imagesearch.datas

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PhotoInfo(
    @SerializedName("id")
    @Expose
    val id : String,
    @SerializedName("owner")
    @Expose
    val owner : String,
    @SerializedName("secret")
    @Expose
    val secret : String,
    @SerializedName("server")
    @Expose
    val server : Int,
    @SerializedName("farm")
    @Expose
    val farm: Int,
    @SerializedName("title")
    @Expose
    val title : String,
    @SerializedName("ispublic")
    @Expose
    val ispublic : String,
    @SerializedName("isfriend")
    @Expose
    val isfriend : String,
    @SerializedName("isfamily")
    @Expose
    val isfamily : String,
)  : Parcelable{



}