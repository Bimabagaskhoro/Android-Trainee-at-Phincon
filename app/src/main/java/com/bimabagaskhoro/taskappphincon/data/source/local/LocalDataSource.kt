package com.bimabagaskhoro.taskappphincon.data.source.local

import com.bimabagaskhoro.taskappphincon.data.source.local.db.notification.NotificationDao
import com.bimabagaskhoro.taskappphincon.data.source.local.db.trolley.CartDao
import com.bimabagaskhoro.taskappphincon.data.source.local.model.CartEntity
import com.bimabagaskhoro.taskappphincon.data.source.local.model.DataTrolley
import com.bimabagaskhoro.taskappphincon.data.source.local.model.NotificationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val dao: CartDao,
    private val notificationDao: NotificationDao
) {
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


    /**
     * notification
     */

    suspend fun saveNotification(notification: NotificationEntity) {
        notificationDao.insertNotification(notification)
    }

    fun getAllNotification(): Flow<List<NotificationEntity>> {
        return notificationDao.getAllNotification()
    }

    fun countItemNotification(): Int {
        return notificationDao.countItems()
    }

    fun getTotalNotification(): Int {
        return notificationDao.getTotalNotification()
    }

    fun getTotalIsReadNotification(): Int {
        return notificationDao.getTotalIsReadNotification()
    }


    fun updateTotalNotification(totalNotification: Int, id: Int): Int {
        return notificationDao.updateTotalNotification(totalNotification, id)
    }

    fun isRead(state: Int, id: Int): Int {
        return notificationDao.isRead(state, id)
    }
}