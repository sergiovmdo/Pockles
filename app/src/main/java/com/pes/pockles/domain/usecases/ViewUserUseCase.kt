package com.pes.pockles.domain.usecases

import com.pes.pockles.model.User
import androidx.lifecycle.LiveData
import com.pes.pockles.data.Resource
import com.pes.pockles.data.repository.UserRepository
import com.pes.pockles.model.ViewUser
import javax.inject.Inject

class ViewUserUseCase @Inject constructor(val repository: UserRepository) {
    fun execute(id: String): LiveData<Resource<ViewUser>> {
        return repository.getUser(id)
    }
}