package com.pes.pockles.view.ui.aboutus

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.pes.pockles.R
import com.pes.pockles.databinding.ActivityAboutusBinding
import com.pes.pockles.view.ui.base.BaseActivity

class AboutUsActivity : BaseActivity() {

    private lateinit var binding: ActivityAboutusBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_aboutus)
        binding.lifecycleOwner = this

        // Add back-button to toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        // Action for back-button on toolbar
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}