package com.pes.pockles.di.component

import android.app.Application
import com.pes.pockles.PocklesApplication
import com.pes.pockles.di.modules.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

/**
 * Main component of the application and the one injected into the application scope.
 *
 * It contains all the modules the app needs.
 */

@Singleton
@Component(
    modules = [
        AppModule::class,
        DaoModule::class,
        AndroidInjectionModule::class,
        ActivitiesModule::class,
        ViewModelModule::class,
        ServiceModule::class
    ]
)
interface AppComponent : AndroidInjector<PocklesApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}