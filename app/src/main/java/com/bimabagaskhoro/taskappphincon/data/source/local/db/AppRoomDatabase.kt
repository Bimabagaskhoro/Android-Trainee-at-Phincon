package com.bimabagaskhoro.taskappphincon.data.source.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bimabagaskhoro.taskappphincon.data.source.local.db.dao.CartDao
import com.bimabagaskhoro.taskappphincon.data.source.local.db.dao.NotificationDao
import com.bimabagaskhoro.taskappphincon.data.source.local.model.CartEntity
import com.bimabagaskhoro.taskappphincon.data.source.local.model.NotificationEntity

@Database(entities = [CartEntity::class, NotificationEntity::class], version = 1)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun notificationDao(): NotificationDao
}