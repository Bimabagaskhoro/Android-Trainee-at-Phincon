package com.bimabagaskhoro.taskappphincon.data.source.local.db.notification

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
    fun insertNotification(notificationEntity: NotificationEntity)

    @Query("SELECT * FROM $NOTIFICATION_TABLE ORDER BY id ASC")
    fun getAllNotification(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM $NOTIFICATION_TABLE")
    fun countItems(): Int

    @Query("SELECT SUM(total_notification) FROM $NOTIFICATION_TABLE")
    fun getTotalNotification(): Int

    @Query("UPDATE $NOTIFICATION_TABLE SET total_notification = :total_notification WHERE id = :id")
    fun updateTotalNotification(total_notification: Int, id: Int): Int

    @Query("UPDATE $NOTIFICATION_TABLE SET isRead = :state WHERE id = :id")
    fun isRead(state: Int, id: Int): Int

    @Query("SELECT SUM(isRead) FROM $NOTIFICATION_TABLE")
    fun getTotalIsReadNotification(): Int

    /**
     *
     */
    @Query("DELETE FROM $NOTIFICATION_TABLE WHERE is_check = 1")
    fun deleteNotification() : Int

    @Query("UPDATE $NOTIFICATION_TABLE SET is_check = :state WHERE id = :id")
    fun updateCheckNotification(id: Int, state : Int): Int

    @Query("SELECT * FROM $NOTIFICATION_TABLE WHERE is_check = 1")
    fun getAllCheckedNotification(): Flow<List<NotificationEntity>>

    @Query("UPDATE $NOTIFICATION_TABLE SET isRead = :state")
    fun checkAllNotification(state: Int) : Int

    @Query("UPDATE $NOTIFICATION_TABLE SET is_state = :state")
    fun viewCheckBoxAnimation(state : Int): Int

    @Query("DELETE FROM $NOTIFICATION_TABLE WHERE id = :id")
    suspend fun deleteNotification(id: Int)
}