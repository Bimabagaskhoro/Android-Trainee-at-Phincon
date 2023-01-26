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

//    @Query("UPDATE quantity FROM $CART_TABLE WHERE id = :id")
//    suspend fun updateQuantity(id: Int)

    @Query("SELECT * FROM $CART_TABLE ORDER BY id ASC")
    fun getAllCarts(): Flow<List<CartEntity>>

    @Query("SELECT COUNT(*) FROM $CART_TABLE")
    fun countItems(): Int

    @Query("SELECT harga FROM $CART_TABLE WHERE id = :id")
    fun getPrice(id: Int): String

//    @Update
//    fun updateCart(cartEntity: CartEntity)
//
//    @Query("SELECT * FROM $CART_TABLE WHERE id LIKE :id")
//    fun getCarts(id : Int) : CartEntity


}