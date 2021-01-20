package com.pes.pockles.model

data class NewMessage(
    var text: String,
    val chatId: String?,
    val pockId: String?
)