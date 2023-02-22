package com.bimabagaskhoro.phincon.core.data.source.remote.response.auth

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseLogin(
    @SerializedName("success")
    val success: SuccessLogin
) : Parcelable

@Parcelize
data class SuccessLogin(

    @SerializedName("access_token")
    val access_token: String? = null,

    @SerializedName("refresh_token")
    val refresh_token: String? = null,

    @SerializedName("data_user")
    val data_user: DataUser,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("status")
    val status: Int? = null
) : Parcelable

@Parcelize
data class DataUser(

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("path")
    val path: String? = null,

    @SerializedName("gender")
    val gender: Int? = null,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("email")
    val email: String? = null
) : Parcelable
