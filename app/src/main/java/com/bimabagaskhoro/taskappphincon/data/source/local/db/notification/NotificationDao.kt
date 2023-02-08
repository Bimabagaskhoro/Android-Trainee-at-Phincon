package com.bimabagaskhoro.taskappphincon.data.source.local.db.notification

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bimabagaskhoro.taskappphincon.data.source.local.model.NotificationEntity
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
}