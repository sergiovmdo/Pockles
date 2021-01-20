package com.pes.pockles.di.injector

import com.pes.pockles.PocklesApplication
import com.pes.pockles.di.component.AppComponent
import com.pes.pockles.di.component.DaggerAppComponent

/**
 * Injects the dependencies into the given [app] instance
 */
fun initInjector(app: PocklesApplication) {
    val appComponent: AppComponent = DaggerAppComponent.builder().application(app).build()
    appComponent.inject(app)
}
