package com.bimabagaskhoro.taskappphincon.data.source.local.model.cart

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bimabagaskhoro.taskappphincon.utils.Constant.Companion.CART_TABLE

@Entity(tableName = CART_TABLE)
data class CartEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "name_product")
    val name_product: String = "",

    @ColumnInfo(name = "harga")
    val harga: String = "",

    @ColumnInfo(name = "image")
    val image: String = "",

    @ColumnInfo(name = "quantity")
    val quantity: Int = 0
)
