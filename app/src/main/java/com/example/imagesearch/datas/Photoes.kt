package com.example.imagesearch.datas

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Photoes(
    @SerializedName("photo")
    @Expose
    val list : List<PhotoInfo>
) : Parcelable {
}