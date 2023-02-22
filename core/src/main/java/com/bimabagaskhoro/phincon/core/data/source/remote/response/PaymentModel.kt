package com.bimabagaskhoro.phincon.core.data.source.remote.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentModel(
	val data: List<DataItem>,
	val id: String? = null,
	val type: String? = null,
	val order: Int? = null
) : Parcelable

@Parcelize
data class DataItem(
	val name: String? = null,
	val id: String? = null,
	val order: Int? = null,
	val status: Boolean? = null
): Parcelable

