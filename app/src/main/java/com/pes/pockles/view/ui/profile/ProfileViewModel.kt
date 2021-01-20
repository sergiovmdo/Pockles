package com.pes.pockles.view.ui.profile

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.pes.pockles.data.repository.UserRepository
import com.pes.pockles.domain.usecases.LogoutUseCase
import com.pes.pockles.model.User
import com.pes.pockles.util.livedata.Event
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    var repository: UserRepository,
    var logoutUseCase: LogoutUseCase
) : ViewModel() {

    lateinit var userdata: User

    init {
        repository.reloadUser()
    }

    private val _navigateToHistory = MutableLiveData<Event<Boolean>>()
    val navigateToHistory: LiveData<Event<Boolean>>
        get() = _navigateToHistory

    private val doLogout = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Event<Boolean>>
        get() = Transformations.switchMap(doLogout) {
            if (it) {
                logoutUseCase.execute()
            } else {
                val result = MutableLiveData<Event<Boolean>>()
                result.value = Event(false)
                result
            }
        }

    val user: LiveData<User> = repository.getUser()

    fun refresh() {
        repository.reloadUser()
    }

    fun navigateToHistoryOnClick(v: View) {
        _navigateToHistory.value = Event(true)
    }


    fun logout(v: View) {
        doLogout.value = true
    }
}