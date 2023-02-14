package com.bimabagaskhoro.taskappphincon.firebase

data class PaymentModel(
	val data: List<DataItem>,
	val id: String? = null,
	val type: String? = null,
	val order: Int? = null
)

data class DataItem(
	val name: String? = null,
	val id: String? = null,
	val order: Int? = null,
	val status: Boolean? = null
)

