package com.bimabagaskhoro.taskappphincon.data.source.local.model.product

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "product")
data class ProductEntity(
    @PrimaryKey
    val id: Int,
    val date: String,
    val image: String,
    val name_product: String,
    val harga: String,
    val size: String,
    val rate: Int,
    val weight: String,
    val stock: Int,
    val type: String,
    val desc: String
) : Parcelable
