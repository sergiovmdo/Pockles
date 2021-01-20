package com.pes.pockles.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pes.pockles.data.database.dao.UserDao
import com.pes.pockles.model.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}