package com.bimabagaskhoro.taskappphincon.data.source.repository.local

import com.bimabagaskhoro.taskappphincon.data.source.local.model.CartEntity
import com.bimabagaskhoro.taskappphincon.data.source.local.model.NotificationEntity
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    /**
     * trolley
     */
    fun getAllProduct(): Flow<List<CartEntity>>
    fun getAllCheckedProduct(): Flow<List<CartEntity>>
    suspend fun addProductToTrolley(trolley: CartEntity)
    suspend fun updateProductData(quantity: Int?, itemTotalPrice: Int?, id: Int?)
    suspend fun updateProductIsCheckedAll(isChecked: Boolean)
    suspend fun updateProductIsCheckedById(isChecked: Boolean, id: Int?)
    suspend fun deleteProductByIdFromTrolley(id: Int?)

    /**
     * notification
     */
    suspend fun insertNotification(notificationEntity: NotificationEntity)
    fun getAllNotification(): Flow<List<NotificationEntity>>
    suspend fun updateReadNotification(isRead: Boolean, id: Int?)
    suspend fun setAllReadNotification(isRead: Boolean)
    suspend fun updateCheckedNotification(isChecked: Boolean, id: Int?)
    suspend fun setAllUncheckedNotification(isChecked: Boolean)
    suspend fun deleteNotification(isChecked: Boolean)
}