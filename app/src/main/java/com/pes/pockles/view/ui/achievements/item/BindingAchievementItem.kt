package com.pes.pockles.view.ui.achievements.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.pes.pockles.R
import com.pes.pockles.databinding.AchievementItemBinding
import com.pes.pockles.model.Achievement

class BindingAchievementItem (val achievement: Achievement) : AbstractBindingItem<AchievementItemBinding>() {

    override val type: Int
        get() = R.id.fastadapter_item

    override fun bindView(binding: AchievementItemBinding, payloads: List<Any>) {
        binding.achievement = achievement
    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): AchievementItemBinding {
        return AchievementItemBinding.inflate(inflater, parent, false)
    }
}