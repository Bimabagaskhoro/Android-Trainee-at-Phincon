package com.bimabagaskhoro.taskappphincon.data.source.local.db.notification

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bimabagaskhoro.taskappphincon.data.source.local.model.CartEntity
import com.bimabagaskhoro.taskappphincon.data.source.local.model.NotificationEntity

@Database(entities = [NotificationEntity::class], version = 1)
abstract class NotificationDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
}