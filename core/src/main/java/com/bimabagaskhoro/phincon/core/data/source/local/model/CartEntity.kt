package com.bimabagaskhoro.phincon.core.data.source.local.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bimabagaskhoro.phincon.core.utils.Constant.Companion.CART_TABLE
import kotlinx.android.parcel.Parcelize

@Entity(tableName = CART_TABLE)
data class CartEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int? = null,

    @ColumnInfo(name = "image")
    val image: String?,

    @ColumnInfo(name = "name_product")
    val nameProduct: String?,

    @ColumnInfo(name = "quantity")
    val quantity: Int? = 1,

    @ColumnInfo(name = "price")
    val price: String?,

    @ColumnInfo(name = "item_total_price")
    val itemTotalPrice: Int?,

    @ColumnInfo(name = "stock")
    val stock: Int? = null,

    @ColumnInfo(name = "is_checked")
    val isChecked: Boolean = false,

    @ColumnInfo(name = "is_state")
    val isState: Int? = null,
)

@Parcelize
data class DataTrolley(
    @ColumnInfo(name = "id")
    val id: Int? = null,
    @ColumnInfo(name = "quantity")
    val quantity: Int? = null,
) : Parcelable