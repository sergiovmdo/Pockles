package com.pes.pockles.util.textinput

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout

/**
 * Cleans the available errors on a [TextInputLayout] when writing new data
 */
open class CleanErrorWatcher constructor(private val inputLayout: TextInputLayout) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        inputLayout.isErrorEnabled = false
        inputLayout.error = null
    }

}