package com.bimabagaskhoro.phincon.core.data.source.remote.response.auth

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
    val path: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("status")
    val status: Int? = null
) : Parcelable
