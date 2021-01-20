package com.pes.pockles.view.ui.login.register

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pes.pockles.data.Resource
import com.pes.pockles.data.repository.UserRepository
import com.pes.pockles.data.storage.StorageManager
import com.pes.pockles.model.CreateUser
import com.pes.pockles.model.User
import com.pes.pockles.util.livedata.Event
import timber.log.Timber
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class RegisterIconViewModel @Inject constructor(
    private var userRepository: UserRepository,
    private val storageManager: StorageManager
) : ViewModel() {

    val user = MutableLiveData<CreateUser>()

    fun setUser(user: CreateUser) {
        this.user.value = user
    }

    fun registerUser(): LiveData<Event<Boolean>> {
        val mediatorLiveData = MediatorLiveData<Event<Boolean>>()

        user.value?.let {
            mediatorLiveData.addSource(userRepository.createUser(it)) { resource ->
                when (resource) {
                    is Resource.Success<User> -> {
                        userRepository.saveUser(resource.data!!)
                        userRepository.saveFCMToken()
                        mediatorLiveData.value = Event(true)
                    }
                    is Resource.Error -> {
                        Timber.d(resource.exception)
                        mediatorLiveData.value = Event(false)
                    }
                }
            }
        }
        return mediatorLiveData
    }

    fun uploadMedia(bitmap: Bitmap): LiveData<Resource<String>> {
        val blob = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, blob)
        return storageManager.uploadMedia(blob.toByteArray(), childReference = "profileImages");
    }

    fun setImageUrl(data: String) {
        val u = user.value
        u?.let {
            it.profileImageUrl = data
            user.value = it
        }
    }

}