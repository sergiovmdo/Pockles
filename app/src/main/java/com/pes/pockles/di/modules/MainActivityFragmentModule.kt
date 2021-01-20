package com.pes.pockles.di.modules


import com.pes.pockles.view.ui.chat.AllChatsFragment
import com.pes.pockles.view.ui.notifications.NotificationsFragment
import com.pes.pockles.view.ui.map.MapFragment
import com.pes.pockles.view.ui.profile.ProfileFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Module that contains all the fragments the main activity shows.
 *
 * If necessary more, they must be in the *FragmentModule of their correspondent activity
 * and then indicated in the [ActivitiesModule]
 */
@Module
abstract class MainActivityFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeMapFragment(): MapFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeChatFragment(): AllChatsFragment

    @ContributesAndroidInjector
    abstract fun contributeNotificationsFragment(): NotificationsFragment
}