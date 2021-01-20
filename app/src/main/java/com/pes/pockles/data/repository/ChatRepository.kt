package com.pes.pockles.data.repository

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.pes.pockles.data.Resource
import com.pes.pockles.data.api.ApiService
import com.pes.pockles.model.Chat
import com.pes.pockles.model.Message
import com.pes.pockles.model.NewMessage
import io.reactivex.functions.Function
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private var apiService: ApiService
) : BaseRepository(apiService) {

    private val observers = mutableMapOf<String, (Message) -> Unit>()

    fun getAllChats(): LiveData<Resource<List<Chat>>> {
        return callApi(Function { apiService -> apiService.allChats() })
    }

    fun getAllChatMessages(chatId: String): LiveData<Resource<List<Message>>> {
        return callApi(Function { apiService -> apiService.allMessageChat(chatId) })
    }

    fun getChatFromPock(pockId: String): LiveData<Resource<List<Message>>> {
        return callApi(Function { apiService -> apiService.allMessageChat(pockId, true) })
    }

    fun newMessage(message: NewMessage): LiveData<Resource<Message>> {
        return callApi(Function { apiService -> apiService.newMessage(message) })
    }

    fun observe(chatId: String, observer: (m: Message) -> Unit) {
        observers[chatId] = observer
    }

    fun removeObserver(chatId: String) {
        observers.remove(chatId)
    }

    fun onMessageReceived(message: Message): Boolean {
        if (observers.contains(message.chatId)) {
            observers[message.chatId]!!(message)
            return true
        } else if (message.senderId == FirebaseAuth.getInstance().uid) {
            return true
        }
        return false
    }


}