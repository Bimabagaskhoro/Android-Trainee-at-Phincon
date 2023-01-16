package com.bimabagaskhoro.taskappphincon.data.source.response.auth

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
	val access_token: String,

	@SerializedName("refresh_token")
	val refresh_token: String,

	@SerializedName("data_user")
	val data_user: DataUser,

	@SerializedName("message")
	val message: String,

	@SerializedName("status")
	val status: Int
) : Parcelable

@Parcelize
data class DataUser(

	@SerializedName("image")
	val image: String,

	@SerializedName("path")
	val path: String,

	@SerializedName("gender")
	val gender: Int,

	@SerializedName("phone")
	val phone: String,

	@SerializedName("name")
	val name: String,

	@SerializedName("id")
	val id: Int,

	@SerializedName("email")
	val email: String
) : Parcelable
