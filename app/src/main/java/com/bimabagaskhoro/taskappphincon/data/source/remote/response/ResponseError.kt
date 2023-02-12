package com.bimabagaskhoro.taskappphincon.data.source.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

//
// error
@Parcelize
data class ResponseError(
    @SerializedName("error")
    val error: ErrorResponse
) : Parcelable

@Parcelize
data class ErrorResponse(
    @SerializedName("message")
    val message: String? = null,

    @SerializedName("status")
    val status: Int? = null
) : Parcelable
