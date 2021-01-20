package com.pes.pockles.view.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.pes.pockles.data.Resource
import com.pes.pockles.data.loading
import com.pes.pockles.data.repository.ChatRepository
import com.pes.pockles.domain.usecases.ChatMessagesUseCase
import com.pes.pockles.domain.usecases.NewMessageUseCase
import com.pes.pockles.model.ChatData
import com.pes.pockles.model.Message
import com.pes.pockles.model.NewMessage
import javax.inject.Inject

class ChatViewModel @Inject constructor(
    var useCase: ChatMessagesUseCase,
    var useCaseNewMessage: NewMessageUseCase,
    private var chatRepository: ChatRepository
) : ViewModel() {

    private var chatId: String? = null
    val messages: LiveData<Resource<MutableList<Message>>>
        get() = _messages

    private val _messages = MediatorLiveData<Resource<MutableList<Message>>>()

    val newMsg: LiveData<Resource<Message>>
        get() = _newMsg

    private val _newMsg = MediatorLiveData<Resource<Message>>()

    fun setUpNotificationObserver(chatID: String) {
        chatId = chatID
        chatRepository.observe(chatID, ::newMessageReceived)
    }

    private fun newMessageReceived(m: Message) {
        val messageList = _messages.value
        messageList?.data!!.add(m)
        _messages.postValue(messageList)
    }

    fun postMessage(message: NewMessage) {
        val newmessage = useCaseNewMessage.execute(message)
        _newMsg.addSource(newmessage) {
            _newMsg.value = it
            if (!it.loading) _newMsg.removeSource(newmessage)
        }
    }

    fun refreshMessages(chatInformation: ChatData) {
        val source =
            if (chatInformation.pockId != null) chatRepository.getChatFromPock(chatInformation.pockId!!)
            else useCase.execute(chatInformation.chatId!!)

        _messages.addSource(source) {
            _messages.value = it as Resource<MutableList<Message>>?
            if (!it.loading) _messages.removeSource(source)
        }
    }

    fun finalize() {
        chatId?.let {
            chatRepository.removeObserver(it)
        }
    }
}
