package com.pes.pockles.view.ui.viewpock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pes.pockles.R
import com.pes.pockles.data.Resource
import com.pes.pockles.data.failed
import com.pes.pockles.data.repository.PockRepository
import com.pes.pockles.data.succeeded
import com.pes.pockles.domain.usecases.ViewPockUseCase
import com.pes.pockles.model.Pock
import com.pes.pockles.model.ReportObject
import javax.inject.Inject

class ViewPockViewModel @Inject constructor(
    private var useCase: ViewPockUseCase,
    private val pockRepository: PockRepository
) : ViewModel() {

    private val _internalPock = MediatorLiveData<Resource<Pock>>()

    val pock: LiveData<Resource<Pock>>
        get() = _internalPock

    private val _errorMsg = MutableLiveData<Int>()
    val errorMsg: LiveData<Int>
        get() = _errorMsg

    init {
        _internalPock.value = Resource.Loading<Nothing>()
        _errorMsg.value = null
    }

    fun loadPock(pockId: String) {
        _internalPock.addSource(useCase.execute(pockId)) {
            _internalPock.value = it
            if (it.failed) {
                _errorMsg.value = R.string.error_no_pock
            }
        }
    }

    fun getPock(): Pock? {
        return if (_internalPock.value is Resource.Success<Pock>) {
            (_internalPock.value as Resource.Success<Pock>).data
        } else {
            null
        }
    }

    fun like() {
        if (pock.value!!.succeeded) {
            pock.value?.data?.let {
                val source = if (it.liked) pockRepository.undoLikePock(it.id)
                else pockRepository.likePock(it.id)

                // Fake an update over the pock so the feedback is instant, in case something goes
                // wrong on the API the action will be reverted automatically as the last
                // data available would be the one from the API

                val copy = it.copy()
                it.likes += if (it.liked) -1 else 1
                it.liked = !it.liked
                _internalPock.value = Resource.Success(it)

                _internalPock.addSource(source) { res ->
                    // Do not update the livedata with loading states when a like action has been
                    // performed, it is a silent call
                    if (res.succeeded) {
                        _internalPock.value = res
                    } else if (res.failed) {
                        // Revert to last state in case there was an error (error resource
                        // does not carry any information of the pock)
                        _internalPock.value = Resource.Success(copy)
                        _errorMsg.value = R.string.error_general_like
                    }
                }
            }
        }
    }

    fun report(motivo: String) {
        val data = pock.value?.data?.let {
            if (it.reported) {
                _errorMsg.value = R.string.reported
            } else {
                var send = ReportObject(
                    motive = motivo
                )
                pockRepository.reportPock(it.id, send)
                _errorMsg.value = R.string.reportSend

            }
        }
    }
}