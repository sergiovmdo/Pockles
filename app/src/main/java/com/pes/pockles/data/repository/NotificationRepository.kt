package com.pes.pockles.data.repository

import androidx.lifecycle.LiveData
import com.pes.pockles.data.Resource
import com.pes.pockles.data.api.ApiService
import com.pes.pockles.model.Notification
import javax.inject.Inject
import javax.inject.Singleton
import io.reactivex.functions.Function

@Singleton
class NotificationRepository @Inject constructor(
    private var apiService: ApiService
) : BaseRepository(apiService){

    fun getNotifications(): LiveData<Resource<List<Notification>>> {
        return callApi(Function { apiService -> apiService.notifications() })
    }
}