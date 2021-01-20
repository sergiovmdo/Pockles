package com.pes.pockles.view.ui.viewuser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pes.pockles.data.Resource
import com.pes.pockles.domain.usecases.ViewUserUseCase
import com.pes.pockles.model.ViewUser
import javax.inject.Inject

class ViewUserViewModel @Inject constructor(
    private var useCase: ViewUserUseCase
) : ViewModel() {

    private val _user = MutableLiveData<ViewUser>()
    val user: LiveData<ViewUser>
        get() = _user

    fun getUser(userId: String): LiveData<Resource<ViewUser>>{
        return useCase.execute(userId)
    }

    fun setUser(data: ViewUser?) {
        _user.value = data
    }

}