package com.bimabagaskhoro.taskappphincon.data.source.local

import com.bimabagaskhoro.taskappphincon.data.source.local.db.cart.CartDao
import com.bimabagaskhoro.taskappphincon.data.source.local.model.cart.CartEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val dao: CartDao) {
//    fun saveCart(cart: CartEntity) = dao.insertCart(cart)
//    fun updateCart(cart: CartEntity) = dao.updateCart(cart)
//    fun deleteCart(cart: CartEntity) = dao.deleteCart(cart)
//    fun getCart(id : Int) : CartEntity = dao.getCarts(id)
//    fun getAllCarts() = dao.getAllCarts()

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

    fun getPrice(id: Int): String {
        return dao.getPrice(id)
    }

    fun updateQuantity(quantity: Int, id: Int): Int {
        return dao.updateQuantity(quantity, id)
    }
}