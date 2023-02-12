package com.bimabagaskhoro.taskappphincon.data.source.remote.response.detail

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseDetail(
	@SerializedName("success")
	val success: SuccessDetail
) : Parcelable

@Parcelize
data class SuccessDetail(
	@SerializedName("data")
	val data: DataDetail,

	@SerializedName("message")
	val message: String? = null,

	@SerializedName("status")
	val status: Int? = null
) : Parcelable

@Parcelize
data class DataDetail(
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

	@SerializedName("image_product")
	val image_product: List<ImageProductItem>,

	@SerializedName("id")
	val id: Int? = null,

	@SerializedName("stock")
	val stock: Int? = null,

	@SerializedName("type")
	val type: String? = null,

	@SerializedName("desc")
	val desc: String? = null,

	@SerializedName("isFavorite")
	val isFavorite: Boolean
) : Parcelable

@Parcelize
data class ImageProductItem(
	@SerializedName("image_product")
	val image_product: String? = null,

	@SerializedName("title_product")
	val title_product: String? = null
) : Parcelable
