package com.bimabagaskhoro.taskappphincon.data.source.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bimabagaskhoro.taskappphincon.data.source.local.model.ProductEntity
import com.bimabagaskhoro.taskappphincon.data.source.local.model.ProductKeys

@Database(
    entities = [ProductEntity::class, ProductKeys::class],
    version = 1,
    exportSchema = false
)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun productKeysDao(): ProductKeysDao
}