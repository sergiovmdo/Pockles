package com.pes.pockles.view.ui.login.register

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.pes.pockles.R
import com.pes.pockles.databinding.ActivityRegisterBinding
import com.pes.pockles.util.livedata.EventObserver
import com.pes.pockles.util.textinput.CleanErrorWatcher
import com.pes.pockles.view.ui.base.BaseActivity
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.xw.repo.BubbleSeekBar
import dev.sasikanth.colorsheet.ColorSheet

class RegisterActivity : BaseActivity() {

    private val viewModel: RegisterActivityViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(RegisterActivityViewModel::class.java)
    }

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.nameEditText.addTextChangedListener(CleanErrorWatcher(binding.nameLayout))
        binding.birthDateEditText.addTextChangedListener(CleanErrorWatcher(binding.textInputLayout))

        MaskedTextChangedListener.Companion.installOn(binding.birthDateEditText, "[00]/[00]/[0000]")

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

        binding.accentColorContainer.setOnClickListener {
            ColorSheet().colorPicker(
                colors = resources.getIntArray(R.array.mdcolor_500),
                selectedColor = getSelectedColor(),
                listener = { color ->
                    viewModel.setColor(color)
                })
                .show(supportFragmentManager)
        }

        viewModel.nextRegister.observe(this, EventObserver(::navigateToRegisterIcon))
        viewModel.nextRegisterError.observe(this, Observer {
            when (it.key) {
                RegisterActivityViewModel.RegisterActivityUiFields.NAME_FIELD ->
                    handleNameFieldError(it.error)
                RegisterActivityViewModel.RegisterActivityUiFields.BIRTH_DATE_FIELD ->
                    handleBirthDateFieldError(it.error)
            }
        })
    }

    private fun navigateToRegisterIcon(b: Boolean) {
        if (b) {
            val intent = Intent(this, RegisterActivityIcon::class.java)
            intent.putExtra("createUser", viewModel.user.value)
            startActivity(intent)
        }
    }

    private fun handleNameFieldError(error: Int) {
        binding.nameLayout.isErrorEnabled = true
        binding.nameLayout.error = getString(error)
    }

    private fun handleBirthDateFieldError(error: Int) {
        binding.textInputLayout.isErrorEnabled = true
        binding.textInputLayout.error = getString(error)
    }

    private fun getSelectedColor(): Int? {
        val color = viewModel.user.value?.accentColor ?: return null
        return Color.parseColor(color)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).setTitle(getString(R.string.cancel_register_dialog_title))
            .setMessage(getString(R.string.cancel_register_dialog_description))
            .setPositiveButton(
                getString(R.string.yes)
            ) { _, _ ->
                run {
                    AuthUI.getInstance().delete(this@RegisterActivity)
                    finish()
                }
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}

