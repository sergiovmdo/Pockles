package com.pes.pockles.view.ui.likes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pes.pockles.R
import com.pes.pockles.data.Resource
import com.pes.pockles.data.loading
import com.pes.pockles.data.repository.PockRepository
import com.pes.pockles.data.repository.UserRepository
import com.pes.pockles.model.Pock
import javax.inject.Inject

class LikedPocksViewModel @Inject constructor(
    private var repository: UserRepository,
    private var pockRepository: PockRepository
) : ViewModel() {

    private val _likedPocks = MediatorLiveData<Resource<List<Pock>>>()
    val likedPocks: LiveData<Resource<List<Pock>>>
        get() = _likedPocks

    private val _errorMsg = MutableLiveData<Int>()
    val errorMsg: LiveData<Int>
        get() = _errorMsg

    fun refreshInformation() {
        val source = repository.getLikedPocks()
        _likedPocks.addSource(source) {
            _likedPocks.value = it
            if (it is Resource.Error) {
                _errorMsg.value = R.string.error_obtaining_liked_pocks
            }
        }
    }

    fun removeLike(id: String) {
        val source = pockRepository.undoLikePock(id)
        _likedPocks.value = Resource.Loading(_likedPocks.value!!.data)
        _likedPocks.addSource(source) {
            if (!it.loading) {
                refreshInformation()
                if (it is Resource.Error) {
                    _errorMsg.value = R.string.error_removing_like
                }
            }
        }
    }
}