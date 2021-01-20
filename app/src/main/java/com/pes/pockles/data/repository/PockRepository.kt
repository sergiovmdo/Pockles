package com.pes.pockles.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.pes.pockles.data.Resource
import com.pes.pockles.data.api.ApiService
import com.pes.pockles.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PockRepository @Inject constructor(
    private val apiService: ApiService
) : BaseRepository(apiService) {

    companion object {
        val CACHE_TTL = TimeUnit.MINUTES.toMillis(5)
    }

    private val nearPocksCache = mutableMapOf<Location, Pair<Long, List<Pock>>>()
    private val callsRegister = mutableMapOf<Location, Boolean>()

    fun newPock(pock: NewPock): LiveData<Resource<Pock>> {
        return callApi(Function { apiService -> apiService.newPock(pock) })
    }

    fun getViewPock(id: String): LiveData<Resource<Pock>> {
        return callApi(Function { apiService -> apiService.viewPock(id) })
    }

    /**
     * Keep track of every request made to a specific location [CACHE_TTL]ms
     * so API is not overflown by requests.
     */
    fun getPocks(loc: Location): LiveData<Resource<List<Pock>>> {
        val result = MutableLiveData<Resource<List<Pock>>>()
        var skip = false

        nearPocksCache[loc]?.let {
            it.first.let { timestamp ->
                if (System.currentTimeMillis() - timestamp < CACHE_TTL) {
                    skip = true
                    result.value = Resource.Success(it.second)
                }
            }
        }

        callsRegister[loc]?.let {
            skip = it
        }

        if (!skip) {
            callsRegister[loc] = true
            disposable.add(apiService.getNearPocks(loc.latitude, loc.longitude)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { r ->
                        run {
                            callsRegister[loc] = false
                            nearPocksCache[loc] = Pair(System.currentTimeMillis(), r)
                            result.value = Resource.Success<List<Pock>>(r)
                        }
                    },
                    { error ->
                        run {
                            callsRegister[loc] = false
                            result.value = Resource.Error(error)
                        }
                    }
                )
            )
        } else {
            result.value = Resource.Success(listOf())
        }
        return result;
    }

    /**
     * Explicitly cleans a specific cache value or the entire cache
     */
    fun cleanCache(loc: Location? = null) {
        if (loc == null) {
            nearPocksCache.clear()
        } else {
            nearPocksCache.remove(loc)
        }
    }

    fun getPocksLocation(): LiveData<Resource<List<LatLng>>> {
        return callApi(Function { apiService -> apiService.pocksLocation() })
    }

    fun editPock(id: String, pock: EditedPock): LiveData<Resource<Pock>> {
        return callApi(Function { apiService -> apiService.editPock(id, pock) })
    }

    fun likePock(id: String): LiveData<Resource<Pock>> {
        return callApi(Function { apiService -> apiService.like(id) })
    }

    fun undoLikePock(id: String): LiveData<Resource<Pock>> {
        return callApi(Function { apiService -> apiService.undoLike(id) })
    }

    fun reportPock(id: String, motivo:ReportObject): LiveData<Resource<Pock>> {
        return callApi(Function { apiService -> apiService.report(id,motivo) })
    }
}