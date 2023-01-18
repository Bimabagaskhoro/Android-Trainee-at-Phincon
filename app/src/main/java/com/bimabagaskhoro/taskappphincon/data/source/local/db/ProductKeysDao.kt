package com.bimabagaskhoro.taskappphincon.data.source.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bimabagaskhoro.taskappphincon.data.source.local.model.ProductKeys

@Dao
interface ProductKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeys(feedKeys: List<ProductKeys>)

    @Query("SELECT * FROM product_keys WHERE id = :feedId")
    suspend fun getKeys(feedId: String): ProductKeys?

    @Query("DELETE FROM product_keys")
    suspend fun deleteKeys()
}