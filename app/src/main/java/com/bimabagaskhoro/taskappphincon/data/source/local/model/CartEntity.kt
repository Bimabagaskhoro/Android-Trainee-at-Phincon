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
    val id: Int,

    @ColumnInfo(name = "name_product")
    val name_product: String = "",

    @ColumnInfo(name = "harga")
    val harga: String = "",

    @ColumnInfo(name = "image")
    val image: String = "",

    @ColumnInfo(name = "quantity")
    val quantity: Int = 0,

    @ColumnInfo(name = "is_check")
    var is_check: Int = 0,

    @ColumnInfo(name = "stock")
    val stock: Int = 0,

    @ColumnInfo(name = "total_harga")
    val totalPrice: Int,

    @ColumnInfo(name = "first_price")
    val firstPrice: String = ""
)

@Parcelize
data class DataTrolley(
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "quantity")
    val quantity: Int,
) : Parcelable