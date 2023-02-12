package com.bimabagaskhoro.taskappphincon.data.source.local.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bimabagaskhoro.taskappphincon.utils.Constant.Companion.CART_TABLE
import kotlinx.android.parcel.Parcelize

@Entity(tableName = CART_TABLE)
data class CartEntity(
    @PrimaryKey()
    val id: Int? = null,

    @ColumnInfo(name = "name_product")
    val name_product: String? = null,

    @ColumnInfo(name = "harga")
    val harga: String? = null,

    @ColumnInfo(name = "image")
    val image: String? = null,

    @ColumnInfo(name = "quantity")
    val quantity: Int? = null,

    @ColumnInfo(name = "is_check")
    var is_check: Int? = null,

    @ColumnInfo(name = "stock")
    val stock: Int? = null,

    @ColumnInfo(name = "total_harga")
    val totalPrice: Int? = null,

    @ColumnInfo(name = "first_price")
    val firstPrice: String? = null
)

@Parcelize
data class DataTrolley(
    @ColumnInfo(name = "id")
    val id: Int? = null,
    @ColumnInfo(name = "quantity")
    val quantity: Int? = null,
) : Parcelable