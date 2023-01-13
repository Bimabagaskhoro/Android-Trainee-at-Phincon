package com.bimabagaskhoro.taskappphincon.data.source.response.auth

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseChangePassword(
	val success: SuccessPassword
) : Parcelable

@Parcelize
data class SuccessPassword(
	val message: String,
	val status: Int
) : Parcelable
