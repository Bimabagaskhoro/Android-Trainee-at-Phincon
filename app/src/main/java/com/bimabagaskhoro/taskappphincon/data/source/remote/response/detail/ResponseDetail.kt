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
	val data: Data,

	@SerializedName("message")
	val message: String,

	@SerializedName("status")
	val status: Int
) : Parcelable

@Parcelize
data class Data(
	@SerializedName("date")
	val date: String,

	@SerializedName("image")
	val image: String,

	@SerializedName("name_product")
	val name_product: String,

	@SerializedName("harga")
	val harga: String,

	@SerializedName("size")
	val size: String,

	@SerializedName("rate")
	val rate: Int,

	@SerializedName("weight")
	val weight: String,

	@SerializedName("image_product")
	val image_product: List<ImageProductItem>,

	@SerializedName("id")
	val id: Int,

	@SerializedName("stock")
	val stock: Int,

	@SerializedName("type")
	val type: String,

	@SerializedName("desc")
	val desc: String
) : Parcelable

@Parcelize
data class ImageProductItem(
	@SerializedName("image_product")
	val image_product: String,

	@SerializedName("title_product")
	val title_product: String
) : Parcelable
