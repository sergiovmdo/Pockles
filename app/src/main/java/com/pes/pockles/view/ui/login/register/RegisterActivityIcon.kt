package com.pes.pockles.view.ui.login.register

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.snackbar.Snackbar
import com.pes.pockles.R
import com.pes.pockles.data.Resource
import com.pes.pockles.databinding.ActivityRegister2Binding
import com.pes.pockles.model.CreateUser
import com.pes.pockles.util.livedata.EventObserver
import com.pes.pockles.view.ui.MainActivity
import com.pes.pockles.view.ui.base.BaseActivity
import com.pes.pockles.view.widget.PhotoPicker


class RegisterActivityIcon : BaseActivity() {

    private val viewModel: RegisterIconViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(RegisterIconViewModel::class.java)
    }

    private lateinit var binding: ActivityRegister2Binding
    private var preventMultipleClicks = false

    private val photoPicker = PhotoPicker(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_2)

        binding.changeIconButton.setOnClickListener {
            photoPicker()
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.registerButton.setOnClickListener {
            if (!preventMultipleClicks) {
                preventMultipleClicks = true
                binding.registerButton.showProgress {
                    progressColor = Color.WHITE
                    buttonTextRes = R.string.registering
                }
                viewModel.registerUser().observe(this, EventObserver(::registerUser))
            }
        }

        bindProgressButton(binding.registerButton)
        binding.registerButton.attachTextChangeAnimator()

        val user: CreateUser? = intent.extras?.getParcelable("createUser")
        user?.let {
            viewModel.setUser(it)
        }

        viewModel.user.value?.let {
            Glide.with(this)
                .load(it.profileImageUrl)
                .into(binding.circularImageView)
        }
    }

    private fun registerUser(b: Boolean) {
        preventMultipleClicks = false
        binding.registerButton.hideProgress(R.string.register_create_profile_button_text)
        if (b) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            Snackbar.make(
                binding.containerRegister2,
                getString(R.string.error_creating_profile),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun photoPicker() {
        photoPicker.createPhotoPicker(getString(R.string.upload_image_dialog_title))
    }

    private fun setPhoto(bitmap: Bitmap) {
        viewModel.uploadMedia(bitmap).observe(this, Observer {
            when (it) {
                is Resource.Loading -> binding.loadingView.visibility = View.VISIBLE
                is Resource.Error -> {
                    binding.loadingView.visibility = View.GONE
                    Snackbar.make(
                        binding.containerRegister2,
                        getString(R.string.error_uploading_an_image),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                is Resource.Success<String> -> {
                    binding.circularImageView.setImageBitmap(bitmap)
                    binding.loadingView.visibility = View.GONE
                    viewModel.setImageUrl(it.data!!)
                }
            }
        })
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)
        photoPicker.processResult(reqCode, resultCode, data, { bytes, _ ->
            setPhoto(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
        }, { error ->
            Snackbar.make(binding.containerRegister2, error, Snackbar.LENGTH_LONG).show()
        })
    }
}