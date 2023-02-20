package com.bimabagaskhoro.taskappphincon.data.source.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bimabagaskhoro.taskappphincon.data.source.local.model.CartEntity
import com.bimabagaskhoro.taskappphincon.data.source.local.model.NotificationEntity
import com.bimabagaskhoro.taskappphincon.utils.Constant
import com.bimabagaskhoro.taskappphincon.utils.Constant.Companion.NOTIFICATION_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notificationEntity: NotificationEntity)

    @Query("SELECT * FROM $NOTIFICATION_TABLE ORDER BY is_read, notification_date ASC")
    fun getAllNotification(): Flow<List<NotificationEntity>>

    @Query("UPDATE $NOTIFICATION_TABLE SET is_read = :isRead WHERE id = :id")
    suspend fun updateReadNotification(isRead: Boolean, id: Int?)

    @Query("UPDATE $NOTIFICATION_TABLE SET is_read = :isRead WHERE is_checked = 1")
    suspend fun setAllReadNotification(isRead: Boolean)

    @Query("UPDATE $NOTIFICATION_TABLE SET is_checked = :isChecked WHERE id = :id")
    suspend fun updateCheckedNotification(isChecked: Boolean, id: Int?)

    @Query("UPDATE $NOTIFICATION_TABLE SET is_checked = :isChecked")
    suspend fun setAllUncheckedNotification(isChecked: Boolean)

    @Query("DELETE FROM $NOTIFICATION_TABLE WHERE is_checked = :isChecked")
    suspend fun deleteNotification(isChecked: Boolean)
}