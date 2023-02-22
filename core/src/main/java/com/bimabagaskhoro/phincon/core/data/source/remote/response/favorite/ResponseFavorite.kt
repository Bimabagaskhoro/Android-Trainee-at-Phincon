package com.bimabagaskhoro.phincon.core.data.source.remote.response.favorite

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseFavorite(
	@SerializedName("success")
	val success: Success
) : Parcelable

@Parcelize
data class DataItemFavorite(

	@SerializedName("id")
	val id: Int? = null,

	@SerializedName("date")
	val date: String? = null,

	@SerializedName("image")
	val image: String? = null,

	@SerializedName("name_product")
	val name_product: String? = null,

	@SerializedName("harga")
	val harga: String? = null,

	@SerializedName("size")
	val size: String? = null,

	@SerializedName("rate")
	val rate: Int? = null,

	@SerializedName("weight")
	val weight: String? = null,

	@SerializedName("stock")
	val stock: Int? = null,

	@SerializedName("type")
	val type: String? = null,

	@SerializedName("desc")
	val desc: String? = null
) : Parcelable

@Parcelize
data class Success(

	@SerializedName("data")
	val data: List<DataItemFavorite>,

	@SerializedName("message")
	val message: String? = null,

	@SerializedName("status")
	val status: Int? = null
) : Parcelable
