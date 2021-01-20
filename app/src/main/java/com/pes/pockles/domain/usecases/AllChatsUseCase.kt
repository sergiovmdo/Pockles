package com.pes.pockles.domain.usecases

import androidx.lifecycle.LiveData
import com.pes.pockles.data.Resource
import com.pes.pockles.data.repository.ChatRepository
import com.pes.pockles.model.Chat
import javax.inject.Inject

class AllChatsUseCase @Inject constructor(val repository: ChatRepository) {

    fun execute(): LiveData<Resource<List<Chat>>> {
        return repository.getAllChats()
    }

}