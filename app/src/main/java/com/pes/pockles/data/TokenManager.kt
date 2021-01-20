package com.pes.pockles.data

import android.content.Context
import android.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.internal.InternalTokenResult
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the token state inside the application, updating it if necessary and saving it
 * into the internal storage of the application, concretely to the default [SharedPreferences] of
 * the application.
 */
@Singleton
class TokenManager @Inject constructor(val context: Context) {

    companion object {
        const val TOKEN_KEY = "TOKEN"
    }

    var token: String? = null

    init {
        //https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth.IdTokenListener#public-method-summary
        loadToken()
        FirebaseAuth.getInstance().addIdTokenListener { i: InternalTokenResult ->
            i.token?.let {
                this.saveToken(it)
            }
        }
    }

    fun refreshToken() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            user.getIdToken(true)
        }
    }

    fun refreshTokenSync(): CountDownLatch {
        val user = FirebaseAuth.getInstance().currentUser
        val lock = CountDownLatch(1)
        user?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.token?.let { token ->
                    this.saveToken(token)
                }
            }
            lock.countDown()
        }
        return lock
    }

    private fun loadToken() {
        this.token = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(TOKEN_KEY, null)
    }

    private fun saveToken(token: String) {
        this.token = token
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
    }
}