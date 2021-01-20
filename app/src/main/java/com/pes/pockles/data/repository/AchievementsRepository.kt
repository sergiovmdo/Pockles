package com.pes.pockles.data.repository
import androidx.lifecycle.LiveData
import com.pes.pockles.data.Resource
import com.pes.pockles.data.api.ApiService
import com.pes.pockles.model.Achievement
import javax.inject.Inject
import javax.inject.Singleton
import io.reactivex.functions.Function

    @Singleton
    class AchievementsRepository @Inject constructor(
            private var apiService: ApiService
        ) : BaseRepository(apiService){

            fun getAchievements(): LiveData<Resource<List<Achievement>>> {
                    return callApi(Function { apiService -> apiService.getAchievements()})
               }
        }
