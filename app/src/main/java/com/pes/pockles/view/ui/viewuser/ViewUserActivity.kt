package com.pes.pockles.view.ui.viewuser

import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.pes.pockles.R
import com.pes.pockles.data.Resource
import com.pes.pockles.databinding.ActivityViewUserBinding
import com.pes.pockles.model.ViewUser
import com.pes.pockles.view.ui.base.BaseActivity


class ViewUserActivity : BaseActivity() {

    private lateinit var binding: ActivityViewUserBinding

    private val viewModel: ViewUserViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(ViewUserViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId: String? = intent.extras?.getString("userId")

        if (userId == null) {
            finish()
        }

        setUpWindow()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_user)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        userId?.let { loadContent(it) }

        binding.back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadContent(id: String) {
        viewModel.getUser(id).observe(this, Observer {
            when (it) {
                is Resource.Loading<ViewUser> -> {
                    binding.loading.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.loading.visibility = View.GONE
                }
                is Resource.Success<ViewUser> -> {
                    binding.loading.visibility = View.GONE
                    viewModel.setUser(it.data)
                }
            }
        })
    }

    private fun setUpWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_DIM_BEHIND,
            WindowManager.LayoutParams.FLAG_DIM_BEHIND
        )
        val params = window.attributes
        params.alpha = 1.0f // lower than one makes it more transparent
        params.dimAmount = .6f
        window.attributes = params
        window.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    android.R.color.transparent
                )
            )
        )
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width: Int = size.x
        val height: Int = size.y
        if (height > width) {
            window.setLayout((width * .9).toInt(), (height * .6).toInt())
        } else {
            window.setLayout((width * .7).toInt(), (height * .8).toInt())
        }
    }
}