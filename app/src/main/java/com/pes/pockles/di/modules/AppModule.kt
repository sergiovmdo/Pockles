package com.pes.pockles.di.modules

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.pes.pockles.PocklesApplication
import com.pes.pockles.data.api.ApiManager
import com.pes.pockles.data.api.ApiService
import com.pes.pockles.data.api.TokenAuthenticator
import com.pes.pockles.data.database.AppDatabase
import com.pes.pockles.data.storage.StorageManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Basic module that provides all the necessary for the di to work. As the provided dependencies
 * have not an inject, di does not know how to treat them, thus we must put them here (or on
 * the module it should go).
 */
@Module
class AppModule {

    @Singleton
    @Provides
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, "pocklesDatabase")
            .allowMainThreadQueries()
            .build()
    }

    @Singleton
    @Provides
    fun provideStorageManager(): StorageManager {
        return StorageManager()
    }

    @Singleton
    @Provides
    fun provideApi(tokenAuthenticator: TokenAuthenticator): ApiService {
        return ApiManager(tokenAuthenticator).createApi(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun providesApplication(app: Application): PocklesApplication {
        return app as PocklesApplication
    }

    @Singleton
    @Provides
    fun providesContext(app: Application): Context {
        return app
    }
}