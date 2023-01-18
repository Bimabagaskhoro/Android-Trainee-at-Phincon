package com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseChangeImage(
    @SerializedName("success")
    val success: SuccessImage
) : Parcelable

@Parcelize
data class SuccessImage(
    @SerializedName("path")
    val path: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int
) : Parcelable
