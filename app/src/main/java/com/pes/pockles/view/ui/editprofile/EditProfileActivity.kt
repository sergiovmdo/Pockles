package com.pes.pockles.view.ui.editprofile

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.bottomsheets.BasicGridItem
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.pes.pockles.R
import com.pes.pockles.data.Resource
import com.pes.pockles.databinding.ActivityEditProfileBinding
import com.pes.pockles.model.EditedUser
import com.pes.pockles.model.User
import com.pes.pockles.view.ui.base.BaseActivity
import com.pes.pockles.view.widget.PhotoPicker
import com.xw.repo.BubbleSeekBar
import dev.sasikanth.colorsheet.ColorSheet

class EditProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    private val viewModel: EditProfileViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(EditProfileViewModel::class.java)
    }

    private val photoPicker = PhotoPicker(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        binding.editProfileViewModel = viewModel
        binding.lifecycleOwner = this

        loadContent()

        viewModel.editableContent.observe(this, Observer {
            it?.let { user ->
                Glide.with(this)
                    .load(user.profileImageUrl)
                    .into(binding.profileImage)
            }
        })

        binding.visibilitySeekBar.onProgressChangedListener = object :
            BubbleSeekBar.OnProgressChangedListener {
            override fun onProgressChanged(
                bubbleSeekBar: BubbleSeekBar?,
                progress: Int,
                progressFloat: Float,
                fromUser: Boolean
            ) {
                viewModel.setVisibility(progressFloat)
            }

            override fun getProgressOnActionUp(
                bubbleSeekBar: BubbleSeekBar?,
                progress: Int,
                progressFloat: Float
            ) {
            }

            override fun getProgressOnFinally(
                bubbleSeekBar: BubbleSeekBar?,
                progress: Int,
                progressFloat: Float,
                fromUser: Boolean
            ) {
            }

        }

        binding.usernameInfo.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                viewModel.setUsername(s.toString())
                if (s.isNullOrEmpty()) {
                    binding.usernameInfo.setError("Este campo no puede estar vacío")
                }
            }
        })

        binding.accentColorContainer.setOnClickListener {
            ColorSheet().colorPicker(
                colors = resources.getIntArray(R.array.mdcolor_500),
                selectedColor = getSelectedColor(),
                listener = { color ->
                    viewModel.setColor(color)
                })
                .show(supportFragmentManager)
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.takeProfileImage.setOnClickListener {
            photoPicker()
        }

        binding.saveButton.setOnClickListener {
            if (!viewModel.editableContent.value?.name.isNullOrEmpty()) {
                if (viewModel.isChanged()) {
                    viewModel.save().observe(this, Observer {
                        when (it) {
                            is Resource.Loading -> {
                                binding.saveButton.visibility = View.GONE
                                binding.saveProgressBar.visibility = View.VISIBLE
                            }
                            is Resource.Error -> {
                                binding.saveProgressBar.visibility = View.GONE
                                binding.saveButton.visibility = View.VISIBLE
                                Snackbar.make(
                                    binding.editProfile,
                                    getString(R.string.error_editing_the_profile),
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                            is Resource.Success<User> -> {
                                binding.saveProgressBar.visibility = View.GONE
                                binding.saveButton.visibility = View.VISIBLE
                                Snackbar.make(
                                    binding.editProfile,
                                    getString(R.string.success_editing_the_profile),
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        }
                    })
                } else {
                    Snackbar.make(
                        binding.editProfile,
                        getString(R.string.no_changes_editing_the_profile),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun loadContent() {
        val infoToEdit = intent.extras?.get("editableContent") as EditedUser
        viewModel.loadContent(
            intent.extras?.get("mail") as String,
            intent.extras?.get("birthDate") as String,
            infoToEdit
        )
        binding.visibilitySeekBar.setProgress(infoToEdit.radiusVisibility)
    }

    private fun getSelectedColor(): Int? {
        val color = viewModel.editableContent.value?.accentColor ?: return null
        return Color.parseColor(color)
    }

    private fun photoPicker() {
        photoPicker.createPhotoPicker(
            getString(R.string.upload_image_dialog_title),
            { pos ->
                if (pos == 2) {
                    viewModel.deleteImage()
                }
            },
            BasicGridItem(R.drawable.ic_delete, getString(R.string.delete))
        )
    }

    private fun setPhoto(bitmap: Bitmap) {
        viewModel.uploadMedia(bitmap).observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.loadingView.visibility = View.VISIBLE
                    binding.profileImage.visibility = View.GONE
                    binding.takeProfileImage.visibility = View.GONE
                }
                is Resource.Error -> {
                    binding.loadingView.visibility = View.GONE
                    binding.profileImage.visibility = View.VISIBLE
                    binding.takeProfileImage.visibility = View.VISIBLE
                    Snackbar.make(
                        binding.editProfile,
                        getString(R.string.error_uploading_an_image),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                is Resource.Success<String> -> {
                    binding.loadingView.visibility = View.GONE
                    viewModel.setImageUrl(it.data!!)
                    binding.profileImage.visibility = View.VISIBLE
                    binding.takeProfileImage.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)
        photoPicker.processResult(reqCode, resultCode, data, { bytes, _ ->
            setPhoto(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
        }, { error ->
            Snackbar.make(binding.editProfile, error, Snackbar.LENGTH_LONG).show()
        })
    }

    private fun close() {
        finish()
    }

    override fun onBackPressed() {
        if (viewModel.isChanged()) {
            AlertDialog.Builder(this).setTitle(getString(R.string.cancel_changes_dialog_title))
                .setMessage(getString(R.string.cancel_changes_dialog_description))
                .setPositiveButton(
                    getString(R.string.yes)
                ) { _, _ ->
                    close()
                }
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }.show()
        } else close()
    }
}
