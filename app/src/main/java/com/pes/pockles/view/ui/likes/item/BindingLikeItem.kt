package com.pes.pockles.view.ui.likes.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.pes.pockles.R
import com.pes.pockles.databinding.LikeItemBinding
import com.pes.pockles.model.Pock

class BindingLikeItem(val pock: Pock) : AbstractBindingItem<LikeItemBinding>() {

    override val type: Int
        get() = R.id.fastadapter_item

    override fun bindView(binding: LikeItemBinding, payloads: List<Any>) {
        binding.pock = pock
    }

    override var identifier: Long
        get() = pock.id.hashCode().toLong()
        set(value) {}

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): LikeItemBinding {
        return LikeItemBinding.inflate(inflater, parent, false)
    }
}