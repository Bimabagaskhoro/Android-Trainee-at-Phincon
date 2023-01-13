package com.bimabagaskhoro.taskappphincon.data.source.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import org.json.JSONObject

@Parcelize
data class ResponseLogin(
	val success: SuccessResponse,
) : Parcelable

@Parcelize
data class SuccessResponse(
	val accessToken: String,
	val refreshToken: String,
	val dataUser: DataUser,
	val message: String,
	val status: Int
) : Parcelable

@Parcelize
data class DataUser(
	val image: String,
	val path: String,
	val gender: Int,
	val phone: String,
	val name: String,
	val id: Int,
	val email: String
) : Parcelable

// error
@Parcelize
data class ResponseLoginError(
	val error: ErrorResponse
) : Parcelable

@Parcelize
data class ErrorResponse(
	val message: String,
	val status: Int
) : Parcelable
