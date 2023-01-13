package com.bimabagaskhoro.taskappphincon.data.source.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseRegister(
	val success: SuccessRegister
) : Parcelable

@Parcelize
data class SuccessRegister(
	val message: String,
	val status: Int
) : Parcelable
