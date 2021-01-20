package com.pes.pockles.view.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.pes.pockles.data.Resource
import com.pes.pockles.data.loading
import com.pes.pockles.data.repository.ChatRepository
import com.pes.pockles.domain.usecases.AllChatsUseCase
import com.pes.pockles.model.Chat
import com.pes.pockles.model.Message
import javax.inject.Inject

class AllChatsViewModel @Inject constructor(
    var useCase: AllChatsUseCase,
    private var chatRepository: ChatRepository
) : ViewModel() {

    val chats: LiveData<Resource<List<Chat>>>
        get() = _chats

    private val _chats = MediatorLiveData<Resource<List<Chat>>>()

    init {
        getAllChats()
    }

    fun getAllChats() {
        val source = useCase.execute()
        _chats.addSource(source) {
            _chats.value = it
            if (!it.loading) _chats.removeSource(source)
        }
    }

    fun setUpNotificationObserver() {
        chats.value?.data?.let {
            it.forEach { chat -> chatRepository.observe(chat.id, ::newMessageReceived) }
        }
    }

    private fun newMessageReceived(m: Message) {
        val allChats = _chats.value
        for (chat in allChats?.data!!) {
            if (chat.id == m.chatId) {
                chat.lastMessage = m.text
            }
        }
        _chats.postValue(allChats)
    }

    fun finalize() {
        chats.value?.data?.let {
            it.forEach { chat ->   chatRepository.removeObserver(chat.id)}
        }
    }


}
