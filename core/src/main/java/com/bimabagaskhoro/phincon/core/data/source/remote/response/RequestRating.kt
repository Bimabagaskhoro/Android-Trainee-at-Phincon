package com.bimabagaskhoro.phincon.core.data.source.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RequestRating(
	@SerializedName("rate")
	val rate: String? = null
) : Parcelable
