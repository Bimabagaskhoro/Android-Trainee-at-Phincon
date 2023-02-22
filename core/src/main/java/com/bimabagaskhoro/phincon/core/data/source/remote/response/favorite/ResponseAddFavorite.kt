package com.bimabagaskhoro.phincon.core.data.source.remote.response.favorite

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseAddFavorite(
	@SerializedName("success")
	val success: SuccessAddFav
) : Parcelable

@Parcelize
data class SuccessAddFav(
	@SerializedName("message")
	val message: String? = null,

	@SerializedName("status")
	val status: Int? = null
) : Parcelable
