package com.pes.pockles.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.NonNull
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

/**
 * Custom implementation of [androidx.core.widget.ContentLoadingProgressBar] that extends
 * [me.zhanghai.android.materialprogressbar.MaterialProgressBar] in order to be used with no
 * intrinsec padding, as the official [android.widget.ProgressBar] has an intrinsec padding on
 * top unable to remove it
 *
 *
 * One solution is to make scaleY to 4, as internally progressbar has 1/4 height as the total
 * height, this leaves a small margin on top which is appreciable.
 *
 *
 * ContentLoadingProgressBar implements a ProgressBar that waits a minimum time to be dismissed
 * before showing (actually 0). Once visible, the progress bar will be visible for a minimum amount
 * of time to avoid "flashes" in the UI when an event could take a largely variable time to complete
 * (from none, to a user perceivable amount)
 */
class ContentLoadingProgressBar(@NonNull context: Context, attrs: AttributeSet?) :
    MaterialProgressBar(context, attrs, 0) {
    var mStartTime: Long = -1
    var mPostedHide = false
    private val mDelayedHide = Runnable {
        mPostedHide = false
        mStartTime = -1
        visibility = View.GONE
    }
    var mDismissed = false

    constructor(@NonNull context: Context) : this(context, null)


    public override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        removeCallbacks()
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks()
    }

    private fun removeCallbacks() {
        removeCallbacks(mDelayedHide)
    }

    /**
     * Hide the progress view if it is visible. The progress view will not be hidden until it has been
     * shown for at least a minimum show time. If the progress view was not yet visible, cancels
     * showing the progress view.
     */
    @Synchronized
    fun hide() {
        mDismissed = true
        val diff = System.currentTimeMillis() - mStartTime
        if (diff >= MIN_SHOW_TIME || mStartTime == -1L) { // The progress spinner has been shown long enough
            // OR was not shown yet. If it wasn't shown yet,
            // it will just never be shown.
            visibility = View.GONE
        } else { // The progress spinner is shown, but not long enough,
            // so put a delayed message in to hide it when its been
            // shown long enough.
            if (!mPostedHide) {
                postDelayed(mDelayedHide, MIN_SHOW_TIME - diff)
                mPostedHide = true
            }
        }
    }

    @Synchronized
    fun show() { // Reset the start time.
        mStartTime = -1
        mDismissed = false
        removeCallbacks(mDelayedHide)
        mPostedHide = false

        if (!mDismissed) {
            mStartTime = System.currentTimeMillis()
            visibility = View.VISIBLE
        }

    }

    companion object {
        private const val MIN_SHOW_TIME = 500 // ms
    }
}