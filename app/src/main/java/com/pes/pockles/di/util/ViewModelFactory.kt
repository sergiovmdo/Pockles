package com.pes.pockles.di.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * A simple [ViewModelProvider.Factory] where all the [viewModels] are auto-injected by di
 *
 * TODO: Check if the initialization of the view models on the activities/fragment is being made
 * in the correct way
 */
@Singleton
class ViewModelFactory @Inject constructor(private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        viewModels[modelClass]?.get() as T
}
