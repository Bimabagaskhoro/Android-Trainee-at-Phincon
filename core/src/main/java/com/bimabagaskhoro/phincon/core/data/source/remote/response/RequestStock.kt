package com.bimabagaskhoro.phincon.core.data.source.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RequestStock(
    @SerializedName("id_user")
    val id_user: String? = null,

    @SerializedName("data_stock")
    val data_stock: List<DataStockItem>
) : Parcelable

@Parcelize
data class DataStockItem(
    @SerializedName("id_product")
    val id_product: String? = null,

    @SerializedName("stock")
    val stock: Int? = null
) : Parcelable
