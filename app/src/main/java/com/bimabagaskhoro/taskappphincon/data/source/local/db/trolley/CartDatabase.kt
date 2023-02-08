package com.bimabagaskhoro.taskappphincon.data.source.local.db.trolley

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bimabagaskhoro.taskappphincon.data.source.local.model.CartEntity

@Database(entities = [CartEntity::class], version = 1)
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}