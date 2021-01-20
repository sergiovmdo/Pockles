package com.pes.pockles.view.ui.newpock

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.snackbar.Snackbar
import com.pes.pockles.R
import com.pes.pockles.data.Resource
import com.pes.pockles.databinding.ActivityNewPockBinding
import com.pes.pockles.model.Location
import com.pes.pockles.model.Pock
import com.pes.pockles.util.LocationUtils.Companion.getLastLocation
import com.pes.pockles.view.ui.base.BaseActivity
import com.pes.pockles.view.widget.PhotoPicker

class NewPockActivity : BaseActivity() {

    private lateinit var binding: ActivityNewPockBinding
    private val viewModel: NewPockViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(NewPockViewModel::class.java)
    }

    private val photoPicker = PhotoPicker(this)
    private var uploadingPock = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_pock)
        binding.lifecycleOwner = this
        binding.newPockViewModel = viewModel

        initializeObservers()

        binding.closeButton.setOnClickListener {
            finish()
        }

        binding.pockButton.setOnClickListener {
            if (!uploadingPock) {
                uploadingPock = true
                getLastLocation(this, {
                    viewModel.insertPock(Location(it.latitude, it.longitude))
                }, {
                    handleError(true)
                })
            }
        }

        binding.image1button.setOnClickListener {
            viewModel.onSaveImage(1)
            goUploadImage()
        }

        binding.image2button.setOnClickListener {
            viewModel.onSaveImage(2)
            goUploadImage()
        }

        binding.image3button.setOnClickListener {
            viewModel.onSaveImage(3)
            goUploadImage()
        }

        binding.image4button.setOnClickListener {
            viewModel.onSaveImage(4)
            goUploadImage()
        }

        val spinner = binding.categoriesDropdown
        spinner.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.categories)
            )
        )
    }

    private fun handleSuccess() {
        hideLoading()
        Toast.makeText(this, resources.getString(R.string.added_pock_message), Toast.LENGTH_SHORT)
            .show()
        finish()
    }

    private fun handleError(apiError: Boolean) {
        hideLoading()
        if (apiError)
            Toast.makeText(
                this,
                resources.getString(R.string.api_error_message),
                Toast.LENGTH_SHORT
            )
                .show()
        else
            binding.pockContentField.error = resources.getString(R.string.pock_content_error)
    }

    private fun initializeObservers() {
        //It will handle the behavior of the app when we try to insert a pock into DB
        viewModel.networkCallback.observe(
            this,
            Observer { value: Resource<Pock>? ->
                value?.let {
                    when (value) {
                        is Resource.Success<*> -> handleSuccess()
                        is Resource.Error -> {
                            handleError(true)
                        }
                        is Resource.Loading -> showLoading()
                    }
                }
            })
        //In case there are any error
        viewModel.errorHandlerCallback.observe(
            this,
            Observer { value: Boolean ->
                value.let {
                    if (value)
                        handleError(false)
                }
            })

        viewModel.errorSavingImages.observe(this, Observer<Boolean> { saveButtonPressed ->
            if (saveButtonPressed) errorImages()
        })
    }

    private var preventMultipleClicks = false
    private fun showLoading() {
        val inputManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.SHOW_FORCED
        )
        if (!preventMultipleClicks) {
            preventMultipleClicks = true
            binding.pockButton.showProgress {
                progressColor = Color.WHITE
                buttonTextRes = R.string.publishing_pock
            }
        }
    }

    private fun hideLoading() {
        uploadingPock = false
        binding.pockButton.hideProgress(R.string.pockear_button)
    }

    private fun errorImages() {
        Snackbar.make(
            binding.newPock,
            getString(R.string.error_uploading_images),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun goUploadImage() {
        photoPicker.createPhotoPicker(getString(R.string.upload_image_dialog_title))
    }

    private fun setImage(bm: ByteArray, fileExtension: String = "png") {
        //Animation control
        if (viewModel.nImg.value != 4) setVisibilityButtons()
        //Shows in the newPock the image that the user wants to upload
        val bitmap = BitmapFactory.decodeByteArray(bm, 0, bm.size)
        when (viewModel.actImg.value) {
            1 -> binding.image1.setImageBitmap(bitmap)
            2 -> binding.image2.setImageBitmap(bitmap)
            3 -> binding.image3.setImageBitmap(bitmap)
            4 -> binding.image4.setImageBitmap(bitmap)
        }
        //Store in the viewModel the image selected by the user
        viewModel.setBm(bm, fileExtension)
    }

    //Function that controls the animations when the user inserts the images
    private fun setVisibilityButtons() {
        binding.image2button.visibility = View.VISIBLE
        binding.image1.visibility = View.VISIBLE
        binding.image2.visibility = View.VISIBLE
        if (viewModel.nImg.value == 2) {
            binding.image3button.visibility = View.VISIBLE
            binding.image3.visibility = View.VISIBLE
        } else if (viewModel.nImg.value == 3) {
            binding.image4button.visibility = View.VISIBLE
            binding.image4.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)
        photoPicker.processResult(reqCode, resultCode, data, { bytes, extension ->
            setImage(bytes, extension)
        }, { error ->
            Snackbar.make(binding.newPock, error, Snackbar.LENGTH_LONG).show()
        })
    }
}
