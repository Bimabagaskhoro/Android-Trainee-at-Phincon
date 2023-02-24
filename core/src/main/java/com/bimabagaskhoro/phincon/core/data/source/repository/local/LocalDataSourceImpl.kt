package com.bimabagaskhoro.phincon.core.data.source.repository.local

import com.bimabagaskhoro.phincon.core.data.source.local.db.dao.CartDao
import com.bimabagaskhoro.phincon.core.data.source.local.db.dao.NotificationDao
import com.bimabagaskhoro.phincon.core.data.source.local.model.CartEntity
import com.bimabagaskhoro.phincon.core.data.source.local.model.NotificationEntity
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl(
    private val cartDao: CartDao,
    private val notificationDao: NotificationDao
) : LocalDataSource {

    /**
     * trolley
     */

    override fun getAllProduct(): Flow<List<CartEntity>> {
        return cartDao.getAllProduct()
    }

    override fun getAllCheckedProduct(): Flow<List<CartEntity>> {
        return cartDao.getAllCheckedProduct()
    }

    override suspend fun addProductToTrolley(trolley: CartEntity) {
        cartDao.addProductToTrolley(trolley)
    }

    override suspend fun updateProductData(quantity: Int?, itemTotalPrice: Int?, id: Int?) {
        cartDao.updateProductData(quantity = quantity, itemTotalPrice = itemTotalPrice, id = id)
    }

    override suspend fun updateProductIsCheckedAll(isChecked: Boolean) {
        cartDao.updateProductIsCheckedAll(isChecked = isChecked)
    }

    override suspend fun updateProductIsCheckedById(isChecked: Boolean, id: Int?) {
        cartDao.updateProductIsCheckedById(isChecked = isChecked, id = id)
    }

    override suspend fun deleteProductByIdFromTrolley(id: Int?) {
        cartDao.deleteProductByIdFromTrolley(id = id)
    }

    override fun countTrolley(): Int{
        return cartDao.countTrolley()
    }

    /**
     * notification
     */
    override suspend fun insertNotification(notificationEntity: NotificationEntity) {
        notificationDao.insertNotification(notificationEntity)
    }

    override fun getAllNotification(): Flow<List<NotificationEntity>> {
        return notificationDao.getAllNotification()
    }

    override suspend fun updateReadNotification(isRead: Boolean, id: Int?) {
        notificationDao.updateReadNotification(isRead = isRead, id = id)
    }

    override suspend fun setAllReadNotification(isRead: Boolean) {
        notificationDao.setAllReadNotification(isRead = isRead)
    }

    override suspend fun updateCheckedNotification(isChecked: Boolean, id: Int?) {
        notificationDao.updateCheckedNotification(isChecked = isChecked, id = id)
    }

    override suspend fun setAllUncheckedNotification(isChecked: Boolean) {
        notificationDao.setAllUncheckedNotification(isChecked = isChecked)
    }

    override suspend fun deleteNotification(isChecked: Boolean) {
        notificationDao.deleteNotification(isChecked = isChecked)
    }

    override fun countNotification(): Int {
        return notificationDao.countNotification()
    }
}