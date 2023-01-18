package com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseRefreshToken(
    @SerializedName("success")
    val success: SuccessRefreshToken
) : Parcelable

@Parcelize
data class SuccessRefreshToken(
    @SerializedName("access_token")
    val access_token: String,

    @SerializedName("refresh_token")
    val refresh_token: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int
) : Parcelable
