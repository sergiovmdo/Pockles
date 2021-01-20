package com.pes.pockles.view.bindings

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.pes.pockles.data.Resource
import com.pes.pockles.data.loading
import com.pes.pockles.view.widget.ContentLoadingProgressBar

@BindingAdapter("app:loadingResource")
fun loadingResource(view: View, res: Resource<*>) {
    if (res.loading) {
        if (view is ContentLoadingProgressBar) {
            view.show()
        } else {
            view.visibility = View.VISIBLE
        }
    } else {
        if (view is ContentLoadingProgressBar) {
            view.hide()
        } else {
            view.visibility = View.GONE
        }
    }
}

@BindingAdapter("app:srcUrl")
fun srcUrl(imageView: ImageView, url: String?) {
    url?.let {
        Glide.with(imageView.context).load(it).into(imageView)
    }
}

@BindingAdapter("app:snackbarError", "app:snackbarErrorEnabled")
fun snackbarError(view: View, idString: Int, enabled: Boolean) {
    if (enabled) {
        Snackbar.make(
            view,
            idString,
            Snackbar.LENGTH_SHORT
        ).show()
    }
}

@BindingAdapter("app:refreshingResource")
fun refreshingResource(view: SwipeRefreshLayout, res: Resource<*>) {
    view.isRefreshing = res.loading
}