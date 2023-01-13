package com.bimabagaskhoro.taskappphincon.data.source.response.auth

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseRefreshToken(
	val success: SuccessRefreshToken
) : Parcelable

@Parcelize
data class SuccessRefreshToken(
	val accessToken: String,
	val refreshToken: String,
	val message: String,
	val status: Int
) : Parcelable
