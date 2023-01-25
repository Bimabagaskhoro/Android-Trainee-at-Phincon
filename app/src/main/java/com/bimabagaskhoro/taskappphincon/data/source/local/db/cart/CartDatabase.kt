package com.bimabagaskhoro.taskappphincon.data.source.local.db.cart

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bimabagaskhoro.taskappphincon.data.source.local.model.cart.CartEntity

@Database(entities = [CartEntity::class], version = 1)
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}