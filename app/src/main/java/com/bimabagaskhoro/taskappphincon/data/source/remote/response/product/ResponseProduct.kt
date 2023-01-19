package com.bimabagaskhoro.taskappphincon.data.source.remote.response.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseProduct(

    @SerializedName("success")
    val success: Success
) : Parcelable

@Parcelize
data class DataItemProduct(

    @SerializedName("date")
    val date: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("name_product")
    val name_product: String,

    @SerializedName("harga")
    val harga: String,

    @SerializedName("size")
    val size: String,

    @SerializedName("rate")
    val rate: Int,

    @SerializedName("weight")
    val weight: String,

    @SerializedName("stock")
    val stock: Int,

    @SerializedName("type")
    val type: String,

    @SerializedName("desc")
    val desc: String
) : Parcelable

@Parcelize
data class Success(

    @SerializedName("data")
    val data: List<DataItemProduct>,

    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int
) : Parcelable
