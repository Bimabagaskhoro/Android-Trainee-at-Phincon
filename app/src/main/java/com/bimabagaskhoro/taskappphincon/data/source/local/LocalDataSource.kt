package com.bimabagaskhoro.taskappphincon.data.source.local

import android.util.Log
import androidx.lifecycle.LiveData
import com.bimabagaskhoro.taskappphincon.data.source.local.db.cart.CartDao
import com.bimabagaskhoro.taskappphincon.data.source.local.model.cart.CartEntity
import com.bimabagaskhoro.taskappphincon.data.source.local.model.cart.DataTrolley
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val dao: CartDao) {
    suspend fun saveCart(cart: CartEntity) {
        dao.insertCart(cart)
    }

    fun getAllCarts(): Flow<List<CartEntity>> {
        return dao.getAllCarts()
    }

    suspend fun deleteCart(id: Int) {
        dao.deleteCart(id)
    }

    fun countItems(): Int {
        return dao.countItems()
    }

    fun updateQuantity(quantity: Int, id: Int, newTotalPrice: Int): Int {
        return dao.updateQuantity(quantity, id, newTotalPrice)
    }

    fun updatePriceCard(price: Int, id: Int): Int {
        return dao.updatePriceCard(price, id)
    }

    fun getCheckedTrolley(): List<DataTrolley>? {
        return dao.getTrolleyChecked()
    }

    fun checkALl(state: Int): Int {
        return dao.checkAll(state)
    }

    fun updateCheck(id: Int, state: Int): Int {
        return dao.updateCheck(id, state)
    }

    fun getTotalHarga(): Int {
        return dao.getTotalPriceChecked()
    }

    fun deleteCheckedTrolley(): Int? {
        return dao.deleteTrolley()
    }

    fun getAllCheckedProductFromTrolly(): Flow<List<CartEntity>> {
        return dao.getAllCheckedProduct()
    }

}