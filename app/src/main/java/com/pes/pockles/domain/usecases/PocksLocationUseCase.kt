package com.pes.pockles.domain.usecases

import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLng
import com.pes.pockles.data.Resource
import com.pes.pockles.data.repository.PockRepository
import javax.inject.Inject

class PocksLocationUseCase @Inject constructor(val repository: PockRepository) {

    fun execute(): LiveData<Resource<List<LatLng>>> {
        return repository.getPocksLocation()
    }

}