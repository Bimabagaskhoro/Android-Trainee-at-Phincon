package com.bimabagaskhoro.taskappphincon.data.source.local.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bimabagaskhoro.taskappphincon.data.source.local.model.ProductEntity

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(feed: List<ProductEntity>)

    @Query("SELECT * FROM product")
    fun getProduct(): PagingSource<Int, ProductEntity>

    @Query("DELETE FROM product")
    suspend fun deleteProduct()
}