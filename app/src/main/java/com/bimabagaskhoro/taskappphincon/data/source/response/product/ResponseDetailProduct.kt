package com.bimabagaskhoro.taskappphincon.data.source.response.product

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseDetailProduct(
	val success: SuccessDetail
) : Parcelable

@Parcelize
data class ImageProductItem(
	val imageProduct: String,
	val titleProduct: String
) : Parcelable

@Parcelize
data class Data(
	val date: String,
	val image: String,
	val nameProduct: String,
	val harga: String,
	val size: String,
	val rate: Int,
	val weight: String,
	val imageProduct: List<ImageProductItem>,
	val stock: Int,
	val type: String,
	val desc: String
) : Parcelable

@Parcelize
data class SuccessDetail(
	val data: Data,
	val message: String,
	val status: Int
) : Parcelable
