package com.pes.pockles.domain.usecases

import androidx.lifecycle.LiveData
import com.pes.pockles.data.Resource
import com.pes.pockles.data.repository.PockRepository
import com.pes.pockles.model.Pock
import javax.inject.Inject

class ViewPockUseCase @Inject constructor(val repository: PockRepository) {
    fun execute(id: String): LiveData<Resource<Pock>> {
        return repository.getViewPock(id)
    }
}