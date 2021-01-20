package com.pes.pockles.model

import com.pes.pockles.data.messaging.NotificationEnum

data class Notification (
    val id: String,
    val title: String,
    val content: String,
    val type: NotificationEnum
)
