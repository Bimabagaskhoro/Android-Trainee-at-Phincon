package com.bimabagaskhoro.phincon.core.data.source.remote.response.auth

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
    val access_token: String? = null,

    @SerializedName("refresh_token")
    val refresh_token: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("status")
    val status: Int? = null
) : Parcelable
