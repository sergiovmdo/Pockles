package com.pes.pockles.domain.usecases

import androidx.lifecycle.LiveData
import com.pes.pockles.data.Resource
import com.pes.pockles.data.repository.PockRepository
import com.pes.pockles.model.NewPock
import com.pes.pockles.model.Pock
import javax.inject.Inject

class NewPockUseCase @Inject constructor(var pockRepository: PockRepository) {

    fun execute(pock: NewPock): LiveData<Resource<Pock>> {
        return pockRepository.newPock(pock)
    }
}