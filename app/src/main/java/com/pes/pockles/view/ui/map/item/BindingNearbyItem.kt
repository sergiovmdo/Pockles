package com.pes.pockles.view.ui.map.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.pes.pockles.R
import com.pes.pockles.databinding.NearbyItemBinding
import com.pes.pockles.model.Pock

class BindingNearbyItem(val pock: Pock) : AbstractBindingItem<NearbyItemBinding>() {
    override val type: Int
        get() = R.id.fastadapter_item

    override fun bindView(binding: NearbyItemBinding, payloads: List<Any>) {
        binding.pock = pock
    }

    override var identifier: Long
        get() = pock.id.hashCode().toLong()
        set(value) {}

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): NearbyItemBinding {
        return NearbyItemBinding.inflate(inflater, parent, false)
    }
}
