package com.pes.pockles.view.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import javax.inject.Inject

/**
 * Base [Fragment] that injects the viewModelFactory. It also sets the fragment as injectable
 * so Dagger can inject the dependencies automatically.
 *
 * It also inflates the layout and assigns it to the binding. No need to write repetitive
 * code anymore
 */
abstract class BaseFragment<DataBinding : ViewDataBinding> : DaggerFragment() {
    @Inject
    protected lateinit var viewModelFactory: ViewModelProvider.Factory

    protected lateinit var binding: DataBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    @LayoutRes
    abstract fun getLayout(): Int
}