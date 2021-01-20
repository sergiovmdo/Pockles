package com.pes.pockles.model

data class Message(
    val id: String,
    val text: String,
    val senderId: String,
    val read: Boolean,
    val date: Long,
    val chatId: String
)