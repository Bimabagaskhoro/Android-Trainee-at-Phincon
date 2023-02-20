package com.bimabagaskhoro.taskappphincon.data.source.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bimabagaskhoro.taskappphincon.utils.Constant
import com.bimabagaskhoro.taskappphincon.utils.Constant.Companion.NOTIFICATION_TABLE

@Entity(tableName = NOTIFICATION_TABLE)
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    @ColumnInfo(name = "notification_title")
    val notificationTitle: String? = null,

    @ColumnInfo(name = "notification_body")
    val notificationBody: String? = null,

    @ColumnInfo(name = "notification_date")
    val notificationDate: String? = null,

    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false,

    @ColumnInfo(name = "is_checked")
    val isChecked: Boolean = false,
)
