package com.pes.pockles.domain.usecases

import androidx.lifecycle.LiveData
import com.pes.pockles.data.Resource
import com.pes.pockles.data.repository.UserRepository
import com.pes.pockles.model.Pock
import javax.inject.Inject

class PocksHistoryUseCase @Inject constructor(val repository: UserRepository) {

    fun execute(): LiveData<Resource<List<Pock>>> {
        return repository.getPocksHistory()
    }

}