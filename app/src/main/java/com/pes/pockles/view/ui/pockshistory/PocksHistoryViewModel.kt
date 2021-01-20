package com.pes.pockles.view.ui.pockshistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.pes.pockles.data.Resource
import com.pes.pockles.data.loading
import com.pes.pockles.domain.usecases.PocksHistoryUseCase
import com.pes.pockles.model.Pock
import javax.inject.Inject

class PocksHistoryViewModel @Inject constructor(
    private var useCase: PocksHistoryUseCase
) : ViewModel() {

    val pocksHistory: LiveData<Resource<List<Pock>>>
        get() = _pocksHistory

    private val _pocksHistory = MediatorLiveData<Resource<List<Pock>>>()

    // Executed when RecyclerView must be updated
    fun refreshInformation() {
        val source = useCase.execute()
        _pocksHistory.addSource(source) {
            _pocksHistory.value = it
            if (!it.loading) _pocksHistory.removeSource(source)
        }
    }
}

