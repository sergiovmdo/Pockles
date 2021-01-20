package com.pes.pockles

import android.app.Application
import com.facebook.stetho.Stetho
import com.google.firebase.FirebaseApp
import com.pes.pockles.di.injector.initInjector
import com.yariksoffice.lingver.Lingver
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject

class PocklesApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
        }

        FirebaseApp.initializeApp(this)

        initInjector(this)

        Lingver.init(this, "es")
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

}