package com.pes.pockles.view.ui

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.pes.pockles.R
import com.pes.pockles.data.repository.UserRepository
import com.pes.pockles.databinding.ActivityMainBinding
import com.pes.pockles.view.ui.base.BaseActivity
import com.pes.pockles.view.ui.newpock.NewPockActivity
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navController = findNavController(R.id.navigationHostFragment)
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        binding.fab.setOnClickListener {
            startActivity(Intent(this, NewPockActivity::class.java))
        }

    }

}
