package com.pes.pockles.domain.usecases

import androidx.lifecycle.LiveData
import com.pes.pockles.data.Resource
import com.pes.pockles.data.repository.PockRepository
import com.pes.pockles.model.Location
import com.pes.pockles.model.Pock
import javax.inject.Inject

class GetNearestPocksUseCase @Inject constructor(var repository: PockRepository) {

    fun execute(value: Location): LiveData<Resource<List<Pock>>> {
        return repository.getPocks(value)
    }
}