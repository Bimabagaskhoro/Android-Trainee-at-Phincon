package com.bimabagaskhoro.taskappphincon.data.source.local.db.product

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bimabagaskhoro.taskappphincon.data.source.local.model.product.ProductEntity
import com.bimabagaskhoro.taskappphincon.data.source.local.model.product.ProductKeys

@Database(
    entities = [ProductEntity::class, ProductKeys::class],
    version = 1,
    exportSchema = false
)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun productKeysDao(): ProductKeysDao
}