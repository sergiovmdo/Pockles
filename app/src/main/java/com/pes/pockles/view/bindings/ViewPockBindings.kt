@file:Suppress("UNCHECKED_CAST")

package com.pes.pockles.view.bindings

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.pes.pockles.R

@BindingAdapter("app:likeTint")
fun likeTint(view: View, like: Boolean) {
    val color = ContextCompat.getColor(
        view.context,
        if (like) R.color.accent else R.color.secondaryButtons
    )

    when (view) {
        is ImageView -> view.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN)
        is TextView -> view.setTextColor(color)
        else -> view.backgroundTintList = ColorStateList.valueOf(color)
    }
}
