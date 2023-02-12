package com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseChangePassword(
    @SerializedName("success")
    val success: SuccessPassword
) : Parcelable

@Parcelize
data class SuccessPassword(
    @SerializedName("message")
    val message: String? = null,

    @SerializedName("status")
    val status: Int? = null
) : Parcelable
