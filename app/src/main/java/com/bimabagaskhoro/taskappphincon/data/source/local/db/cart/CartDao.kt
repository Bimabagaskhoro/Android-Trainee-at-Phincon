package com.bimabagaskhoro.taskappphincon.data.source.local.db.cart

import androidx.room.*
import com.bimabagaskhoro.taskappphincon.data.source.local.model.cart.CartEntity
import com.bimabagaskhoro.taskappphincon.data.source.local.model.cart.DataTrolley
import com.bimabagaskhoro.taskappphincon.utils.Constant.Companion.CART_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCart(cartEntity: CartEntity)

    @Query("DELETE FROM $CART_TABLE WHERE id = :id")
    suspend fun deleteCart(id: Int)

    @Query("UPDATE $CART_TABLE SET quantity = :quantity , total_harga = :newTotalPrice WHERE id = :id")
    fun updateQuantity(quantity: Int, id: Int, newTotalPrice: Int): Int

    @Query("UPDATE $CART_TABLE SET harga = :harga WHERE id = :id")
    fun updatePriceCard(harga: Int, id: Int): Int

    @Query("SELECT * FROM $CART_TABLE ORDER BY id ASC")
    fun getAllCarts(): Flow<List<CartEntity>>

    @Query("SELECT COUNT(*) FROM $CART_TABLE")
    fun countItems(): Int

    @Query("SELECT harga FROM $CART_TABLE WHERE id = :id")
    fun getPrice(id: Int): String

    @Query("SELECT id, quantity FROM $CART_TABLE WHERE is_check = 1")
    fun getTrolleyChecked(): List<DataTrolley>

    @Query("UPDATE $CART_TABLE SET is_check = :state WHERE id = :id")
    fun updateCheck(id: Int, state : Int): Int

    @Query("SELECT SUM(total_harga) FROM $CART_TABLE WHERE is_check = 1")
    fun getTotalPriceChecked() : Int

    @Query("DELETE FROM $CART_TABLE WHERE is_check = 1")
    fun deleteTrolley() : Int

    @Query("SELECT * FROM $CART_TABLE WHERE is_check = 1")
    fun getAllCheckedProduct(): Flow<List<CartEntity>>


    @Query("UPDATE $CART_TABLE SET is_check = :state")
    fun checkAll(state: Int) : Int
}