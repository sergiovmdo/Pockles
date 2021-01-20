package com.pes.pockles.di.modules

import com.pes.pockles.data.messaging.PocklesMessagingService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module abstract class ServiceModule{
    @ContributesAndroidInjector abstract fun contributeFCMService() : PocklesMessagingService
}