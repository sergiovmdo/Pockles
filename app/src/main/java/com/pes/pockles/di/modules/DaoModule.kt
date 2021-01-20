package com.pes.pockles.di.modules

import com.pes.pockles.data.database.AppDatabase
import com.pes.pockles.data.database.dao.UserDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Module that contains all the DAOs of the application.
 */
@Module
class DaoModule {

    @Singleton
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
}