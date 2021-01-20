package com.pes.pockles.view.ui.map

import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.pes.pockles.data.Resource
import com.pes.pockles.domain.usecases.GetNearestPocksUseCase
import com.pes.pockles.domain.usecases.PocksLocationUseCase
import com.pes.pockles.model.Location
import com.pes.pockles.model.Pock
import com.pes.pockles.util.extensions.forceRefresh
import com.pes.pockles.util.livedata.AbsentLiveData
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private var useCaseNearestPocks: GetNearestPocksUseCase,
    private var useCaseAllPocks: PocksLocationUseCase
) : ViewModel() {

    lateinit var categories: Array<String>
    private val _currentLocation = MutableLiveData<Location?>()
    val checkedItems = booleanArrayOf(true, true, true, true, true, true, true, true, true, true)

    init {
        _currentLocation.value = null
    }

    private val _pocks: LiveData<Resource<List<Pock>>?>
        get() = Transformations.switchMap(_currentLocation) { value: Location? ->
            if (value != null) useCaseNearestPocks.execute(value) else AbsentLiveData.create()
        }

    private val internalPocks: MediatorLiveData<Resource<List<Pock>>?> = MediatorLiveData()

    private val _latLngAllPocks: MediatorLiveData<Resource<List<LatLng>>?> = MediatorLiveData()

    val latLngAllPocks: LiveData<Resource<List<LatLng>>?>
        get() = _latLngAllPocks

    fun getPocks(): LiveData<Resource<List<Pock>>?> {
        internalPocks.addSource(_pocks) { value ->
            internalPocks.value = value
        }

        return Transformations.map(internalPocks) { value: Resource<List<Pock>>? ->
            if (value is Resource.Success<List<Pock>>) {
                Resource.Success(value.data!!.filter {
                    if (categories.contains(it.category)) {
                        checkedItems[categories.indexOf(it.category)]
                    } else true
                })
            } else value
        }
    }

    fun updateLocation(loc: android.location.Location) {
        _currentLocation.value = Location(longitude = loc.longitude, latitude = loc.latitude)
    }

    fun setFilterItem(position: Int, status: Boolean) {
        checkedItems[position] = status
        internalPocks.forceRefresh()
    }

    private fun refreshLatLng(): LiveData<Resource<List<LatLng>>?> {
        // Manually refresh the location data by using the last saved data
        // while waiting for the response of the API
        _latLngAllPocks.value = Resource.Loading(_latLngAllPocks.value?.data)
        _latLngAllPocks.addSource(useCaseAllPocks.execute()) { value ->
            _latLngAllPocks.value = value
        }
        return latLngAllPocks
    }

    fun onUpdateHeatMap(heatMapEnabled: Boolean) {
        if (heatMapEnabled) {
            refreshLatLng()
        } else {
            internalPocks.forceRefresh()
        }
    }
}
