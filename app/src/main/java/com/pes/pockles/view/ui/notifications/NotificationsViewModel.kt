package com.pes.pockles.view.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.pes.pockles.data.Resource
import com.pes.pockles.data.loading
import com.pes.pockles.data.repository.NotificationRepository
import com.pes.pockles.model.Notification
import javax.inject.Inject

class NotificationsViewModel @Inject constructor(
    private var repository: NotificationRepository
) : ViewModel() {
    val notifications: LiveData<Resource<List<Notification>>>
        get() = _notifications

    private val _notifications = MediatorLiveData<Resource<List<Notification>>>()

    fun refreshNotifications() {
        val data = repository.getNotifications()
        _notifications.addSource(data) {
            _notifications.value = it
            if (!it.loading) _notifications.removeSource(data)
        }
    }
}