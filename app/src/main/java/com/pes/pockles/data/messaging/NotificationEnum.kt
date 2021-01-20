package com.pes.pockles.data.messaging

import android.app.ActivityManager

import com.pes.pockles.data.repository.RepositoryProvider
import com.pes.pockles.model.Message
import com.pes.pockles.view.ui.chat.ChatActivity

enum class NotificationEnum {
    CHAT {
        override fun onMessageReceived(
            repositoryProvider: RepositoryProvider,
            messageBody: String?,
            title: String?,
            extras: Map<String, String>
        ): Boolean {
            val msg = Message(
                extras["chatId"].toString(),
                extras["text"].toString(),
                extras["senderId"].toString(),
                (extras["read"].toString()).toBoolean(),
                (extras["date"].toString()).toLong(),
                extras["chatId"].toString()
            )
            return repositoryProvider.chatRepository.onMessageReceived(msg)
        }
    },
    TRENDING {
        override fun onMessageReceived(
            repositoryProvider: RepositoryProvider,
            messageBody: String?,
            title: String?,
            extras: Map<String, String>
        ): Boolean {
            return false
        }

    },
    REPORTS {
        override fun onMessageReceived(
            repositoryProvider: RepositoryProvider,
            messageBody: String?,
            title: String?,
            extras: Map<String, String>
        ): Boolean {
            return false
        }

    },
    ACHIEVEMENT {
        override fun onMessageReceived(
            repositoryProvider: RepositoryProvider,
            messageBody: String?,
            title: String?,
            extras: Map<String, String>
        ): Boolean {
            return false
        }

    },
    BAN {
        override fun onMessageReceived(
            repositoryProvider: RepositoryProvider,
            messageBody: String?,
            title: String?,
            extras: Map<String, String>
        ): Boolean {
            return false
        }

    },
    DEFAULT {
        override fun onMessageReceived(
            repositoryProvider: RepositoryProvider,
            messageBody: String?,
            title: String?,
            extras: Map<String, String>
        ): Boolean {
            return false
        }

    };

    abstract fun onMessageReceived(
        repositoryProvider: RepositoryProvider,
        messageBody: String?,
        title: String?,
        extras: Map<String, String>
    ): Boolean


}