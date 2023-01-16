package com.bimabagaskhoro.taskappphincon.data.source.response.auth

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseRegister(
	@SerializedName("success")
	val success: SuccessRegister
) : Parcelable

@Parcelize
data class SuccessRegister(
	@SerializedName("message")
	val message: String,

	@SerializedName("status")
	val status: Int
) : Parcelable
