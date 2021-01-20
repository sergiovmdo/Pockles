package com.pes.pockles.view.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.pes.pockles.R
import com.pes.pockles.databinding.NotificationItemBinding
import com.pes.pockles.model.Notification

class BindingNotificationItem(val notification: Notification) : AbstractBindingItem<NotificationItemBinding>() {

    override val type: Int
        get() = R.id.fastadapter_item

    override fun bindView(binding: NotificationItemBinding, payloads: List<Any>) {
        binding.notification = notification
    }


    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): NotificationItemBinding {
        return NotificationItemBinding.inflate(inflater, parent, false)
    }

}