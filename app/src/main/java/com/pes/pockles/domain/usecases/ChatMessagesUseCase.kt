package com.pes.pockles.domain.usecases

import androidx.lifecycle.LiveData
import com.pes.pockles.data.Resource
import com.pes.pockles.data.repository.ChatRepository
import com.pes.pockles.model.Message
import javax.inject.Inject

class ChatMessagesUseCase @Inject constructor(val repository: ChatRepository) {

    fun execute(id : String): LiveData<Resource<List<Message>>> {
        return repository.getAllChatMessages(id)
    }

}