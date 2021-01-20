package com.pes.pockles.view.ui.base


import android.content.Context
import android.graphics.Rect
import android.preference.PreferenceManager
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject


/**
 * Base Activity that injects the viewModelFactory. It also sets the activity as injectable
 * so Dagger can inject the dependencies automatically
 */
abstract class BaseActivity : DaggerAppCompatActivity() {

    private var keyboardListenersAttached = false
    private var rootLayout: View? = null
    private var isKeyboardShowing = false

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory
    private var locale: String? = null

    private val keyboardLayoutListener = OnGlobalLayoutListener {
        val r = Rect()
        rootLayout!!.getWindowVisibleDisplayFrame(r)
        val screenHeight: Int = rootLayout!!.rootView.height
        val keypadHeight: Int = screenHeight - r.bottom

        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
            if (!isKeyboardShowing) { // keyboard is opened
                isKeyboardShowing = true
                onKeyboardVisibilityChanged(true)
            }
        } else {
            if (isKeyboardShowing) { // keyboard is closed
                isKeyboardShowing = false
                onKeyboardVisibilityChanged(false)
            }
        }
    }

    protected open fun attachKeyboardListeners(view: View) {
        if (keyboardListenersAttached) {
            return
        }
        rootLayout = view
        rootLayout!!.viewTreeObserver.addOnGlobalLayoutListener(
            keyboardLayoutListener
        )
        keyboardListenersAttached = true
    }

    open fun onKeyboardVisibilityChanged(visible: Boolean) {}

    override fun onDestroy() {
        super.onDestroy()
        if (keyboardListenersAttached) {
            rootLayout?.viewTreeObserver?.removeOnGlobalLayoutListener(
                keyboardLayoutListener
            )
        }
    }


    override fun attachBaseContext(newBase: Context) {
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(newBase)
        locale = sharedPreferences.getString("Language", "es")
        super.attachBaseContext(locale?.let { BaseContextWrapper.wrap(newBase, it) })
    }


}