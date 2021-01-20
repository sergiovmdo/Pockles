package com.pes.pockles.util

import com.pes.pockles.model.Chat
import com.pes.pockles.model.Message
import com.pes.pockles.model.Pock
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


object TimeUtils {
    @JvmStatic
    fun getPockTime(pock: Pock): String {
        return getTime(pock.dateInserted, "dd-MMM-yyyy HH:mm")
    }
    @JvmStatic
    fun getChatTime(chat: Chat): String {
        return getTime(chat.date, "dd-MM HH:mm")
    }
    @JvmStatic
    fun getMessageTime(msg: Message): String {
        return getTime(msg.date,"HH:mm")
    }
    private fun getTime(date : Long, pattern : String) : String {
        val df: DateFormat = SimpleDateFormat(
            pattern,
            Locale.getDefault()
        )
        return try {
            df.format(date)
        } catch (ignore: Exception) {
            ""
        }
    }
}