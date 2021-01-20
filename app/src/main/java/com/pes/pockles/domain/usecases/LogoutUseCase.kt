package com.pes.pockles.domain.usecases

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.AuthUI
import com.pes.pockles.data.database.dao.UserDao
import com.pes.pockles.util.livedata.Event
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogoutUseCase @Inject constructor(var context: Context, var userDao: UserDao) {

    fun execute(): LiveData<Event<Boolean>> {
        userDao.clean()
        val result = MutableLiveData<Event<Boolean>>()
        AuthUI.getInstance()
            .signOut(context)
            .addOnSuccessListener {
                result.value = Event(true)
            }.addOnFailureListener {
                Timber.d(it)
                result.value = Event(false)
            }

        return result
    }
}