package com.pes.pockles.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.pes.pockles.databinding.PockImageItemBinding

class ImageGrid : LinearLayout {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private val _list = ArrayList<String>()

    private fun init() {
        orientation = VERTICAL
    }

    fun setImageList(list: List<String>?) {
        list?.let {
            showList(list)
        }
    }

    private fun showList(list: List<String>) {
        if (list.isEmpty()) {
            visibility = View.GONE
            return
        }

        val inflater = LayoutInflater.from(context)

        visibility = View.VISIBLE

        if (list.all { item -> _list.contains(item) }) {
            return
        }

        _list.clear()
        _list.addAll(list)

        removeAllViews()

        list.forEach {
            PockImageItemBinding.inflate(inflater, this, true).url = it
        }

    }
}