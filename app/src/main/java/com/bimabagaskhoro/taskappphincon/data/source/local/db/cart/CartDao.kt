package com.bimabagaskhoro.taskappphincon.data.source.local.db.cart

import androidx.room.*
import com.bimabagaskhoro.taskappphincon.data.source.local.model.cart.CartEntity
import com.bimabagaskhoro.taskappphincon.utils.Constant.Companion.CART_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCart(cartEntity: CartEntity)

    @Query("DELETE FROM $CART_TABLE WHERE id = :id")
    suspend fun deleteCart(id: Int)

    @Query("SELECT * FROM $CART_TABLE ORDER BY id ASC")
    fun getAllCarts(): Flow<List<CartEntity>>

//    @Update
//    fun updateCart(cartEntity: CartEntity)
//
//    @Query("SELECT * FROM $CART_TABLE WHERE id LIKE :id")
//    fun getCarts(id : Int) : CartEntity


}