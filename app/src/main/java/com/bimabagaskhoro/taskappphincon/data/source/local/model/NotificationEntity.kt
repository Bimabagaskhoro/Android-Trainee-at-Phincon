package com.bimabagaskhoro.taskappphincon.data.source.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bimabagaskhoro.taskappphincon.utils.Constant
import com.bimabagaskhoro.taskappphincon.utils.Constant.Companion.NOTIFICATION_TABLE

@Entity(tableName = NOTIFICATION_TABLE)
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "tittle_notification")
    val tittle_notification: String = "",

    @ColumnInfo(name = "body_notification")
    val body_notification: String = "",

    @ColumnInfo(name = "timestamp_notification")
    val timestamp_notification: String = "",

    @ColumnInfo(name = "isRead")
    val isRead: Int = 0,

    @ColumnInfo(name = "total_notification")
    val total_notification: Int = 0

)
