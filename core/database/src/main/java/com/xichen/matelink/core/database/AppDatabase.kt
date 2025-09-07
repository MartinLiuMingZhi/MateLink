package com.xichen.matelink.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xichen.matelink.core.database.dao.UserDao
import com.xichen.matelink.core.database.entity.UserEntity

/**
 * 应用数据库
 */
@Database(
    entities = [
        UserEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "matelink.db"
    }
}
