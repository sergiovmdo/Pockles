package com.pes.pockles.data.repository

import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pes.pockles.data.Resource
import com.pes.pockles.data.api.ApiService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

/**
 * Base repository with helper functions
 */
open class BaseRepository constructor(private var apiService: ApiService) {

    protected val disposable: CompositeDisposable = CompositeDisposable()

    /**
     * Transforms the supplied call through the @param [apiCall] into a @see [LiveData] with
     * a @see [Resource] with the object type of the api call.
     *
     * It encapsulates all the async functionality for an API call.
     *
     * Example usage:
     *
     * In a given repository TestRepository, it must extend this BaseRepository in order to be able
     * to use this helper.
     *
     * ```
     *  class TestRepository : BaseRepository() {...}
     * ```
     *
     * And in every function that want to call an API method:
     * ```
     * fun testMethod(testParameter: String): LiveData<Resource<ReturnType>> {
     *      return callApi(Function { apiService -> apiService.nameOnTheApiService(testParameter) })
     * }
     * ```
     *
     * Note: ReturnType must be the same ReturnType on the @see [ApiService]
     *
     * The LiveData must be read somewhere that is lifecycle-aware (Fragments or Activities), like:
     *
     * ```
     * TestRepository().testMethod("TestString").observe(this, Observer{ data ->
     *      do whatever you want with the data here, note that the data is one of Loading, Success
     *      or Error, so the UI should display accordingly to this states
     * }
     * ```
     *
     * Note: Repositories should not be called directly from the ui part, but for testing is ok.
     *
     * @return @see [LiveData] with a @see [Resource] of the object type of the api call
     */
    fun <T : Any> callApi(@NonNull apiCall: Function<ApiService, Single<T>>): LiveData<Resource<T>> {
        val mutableLiveData: MutableLiveData<Resource<T>> = MutableLiveData()

        mutableLiveData.value = Resource.Loading<Nothing>()

        disposable.add(
            apiCall.apply(apiService)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { result -> mutableLiveData.value = Resource.Success<T>(result) },
                    { error -> mutableLiveData.value = Resource.Error(error) }
                )
        )

        return mutableLiveData

    }
}