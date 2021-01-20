package com.pes.pockles.domain.usecases

import androidx.lifecycle.LiveData
import com.pes.pockles.data.Resource
import com.pes.pockles.data.repository.PockRepository
import com.pes.pockles.model.EditedPock
import com.pes.pockles.model.Pock
import javax.inject.Inject

class EditPockUseCase @Inject constructor(var pockRepository: PockRepository) {
    fun execute(id: String, pock: EditedPock): LiveData<Resource<Pock>> {
        return pockRepository.editPock(id, pock)
    }
}