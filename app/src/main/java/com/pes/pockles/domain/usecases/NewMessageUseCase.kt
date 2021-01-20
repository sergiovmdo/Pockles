package com.pes.pockles.domain.usecases

import androidx.lifecycle.LiveData
import com.pes.pockles.data.Resource
import com.pes.pockles.data.repository.ChatRepository
import com.pes.pockles.data.repository.UserRepository
import com.pes.pockles.model.Message
import com.pes.pockles.model.NewMessage
import com.pes.pockles.model.NewPock
import com.pes.pockles.model.Pock
import javax.inject.Inject

class NewMessageUseCase @Inject constructor(var chatRepository: ChatRepository) {

    fun execute(message: NewMessage): LiveData<Resource<Message>> {
        return chatRepository.newMessage(message)
    }
}