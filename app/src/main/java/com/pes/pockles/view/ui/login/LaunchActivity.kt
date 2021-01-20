package com.pes.pockles.view.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pes.pockles.R
import com.pes.pockles.data.Resource
import com.pes.pockles.view.ui.MainActivity
import com.pes.pockles.view.ui.base.BaseActivity
import com.pes.pockles.view.ui.login.register.RegisterActivity

class LaunchActivity : BaseActivity() {

    private val viewModel: LaunchActivityViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(LaunchActivityViewModel::class.java)
    }

    private lateinit var dialog: MaterialDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            createSignInIntent()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build()
        )

        //Creates the custom layout and binds buttons to login methods
        val customLayout =
            AuthMethodPickerLayout.Builder(R.layout.activity_login)
                .setFacebookButtonId(R.id.facebookButton)
                .setEmailButtonId(R.id.emailButton)
                .setGoogleButtonId(R.id.googleButton)
                .build()

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setAuthMethodPickerLayout(customLayout)
                .setTheme(R.style.Login)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                viewModel.saveToken()
                dialog = MaterialDialog(this)
                    .title(R.string.checking_data)
                    .cancelOnTouchOutside(false)
                viewModel.userExists(user!!.uid)
                    .observe(this, Observer { handleUserExistsResult(it) })
            }
        }
    }

    private fun handleUserExistsResult(value: Resource<Boolean>) {
        dialog.dismiss()
        when (value) {
            is Resource.Loading -> dialog.show()
            is Resource.Success<Boolean> -> {
                if (value.data!!) {
                    viewModel.loadUser()
                    viewModel.saveFCMToken()
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    startActivity(Intent(this, RegisterActivity::class.java))
                }
            }
            is Resource.Error -> {
                dialog.dismiss()
                Toast.makeText(
                    this,
                    getString(R.string.error_try_later),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        private const val RC_SIGN_IN = 123
    }
}