package com.bimabagaskhoro.taskappphincon.data.source.response.product

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseProduct(
	val success: Success
) : Parcelable

@Parcelize
data class DataItem(
	val date: String,
	val image: String,
	val nameProduct: String,
	val harga: String,
	val size: String,
	val rate: Int,
	val weight: String,
	val stock: Int,
	val type: String,
	val desc: String
) : Parcelable

@Parcelize
data class Success(
	val data: List<DataItem>,
	val message: String,
	val status: Int
) : Parcelable
