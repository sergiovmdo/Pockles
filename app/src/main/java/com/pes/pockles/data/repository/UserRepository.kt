package com.pes.pockles.data.repository

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.pes.pockles.data.Resource
import com.pes.pockles.data.api.ApiService
import com.pes.pockles.data.database.AppDatabase
import com.pes.pockles.model.*
import com.pes.pockles.util.AppExecutors
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private var database: AppDatabase,
    private var apiService: ApiService,
    private var executors: AppExecutors
) : BaseRepository(apiService) {

    fun getUser(): LiveData<User> {
        return database.userDao().getUser()
    }

    fun reloadUser() {
        disposable.add(apiService.getUser()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                ::saveUser
            ) { Timber.e(it) })
    }

    fun getUser(id: String): LiveData<Resource<ViewUser>> {
        return callApi(Function { apiService -> apiService.getUserById(id) })
    }

    fun userExists(uid: String): LiveData<Resource<Boolean>> {
        return callApi(Function { apiService -> apiService.userExists(uid) })
    }

    fun createUser(createUser: CreateUser): LiveData<Resource<User>> {
        return callApi(Function { apiService -> apiService.createUser(createUser) })
    }

    fun saveUser(user: User) {
        executors.diskIO().execute { database.userDao().insert(user) }
    }

    fun getPocksHistory(): LiveData<Resource<List<Pock>>> {
        return callApi(Function { apiService -> apiService.pocksHistory() })
    }

    fun getLikedPocks(): LiveData<Resource<List<Pock>>> {
        return callApi(Function { apiService -> apiService.likedPocks() })
    }

    fun insertFCMToken(token: String): LiveData<Resource<Boolean>> {
        return callApi(Function { apiService -> apiService.insertFCMToken(InsertToken(token = token)) })
    }

    fun saveFCMToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                insertFCMToken(token!!)
            })
    }

    fun editProfile(editProfile: EditedUser): LiveData<Resource<User>> {
        return callApi(Function { apiService -> apiService.editProfile(editProfile) })
    }
}