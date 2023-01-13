package com.bimabagaskhoro.taskappphincon.data.source.response.auth

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseChangeImage(
	val success: SuccessImage
) : Parcelable

@Parcelize
data class SuccessImage(
	val path: String,
	val message: String,
	val status: Int
) : Parcelable
