package com.pes.pockles.view.ui.achievements

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.pes.pockles.data.Resource
import com.pes.pockles.data.loading
import com.pes.pockles.data.repository.AchievementsRepository
import com.pes.pockles.model.Achievement
import javax.inject.Inject

class AchievementsViewModel @Inject constructor(
    private var repository: AchievementsRepository
) : ViewModel() {

    private val _achievement = MediatorLiveData<Resource<List<Achievement>>>()
    val achievement: LiveData<Resource<List<Achievement>>>
        get() = _achievement

    fun refreshAchievements() {
        val data = repository.getAchievements()
        _achievement.addSource(data) {
            _achievement.value = it
            if (!it.loading) _achievement.removeSource(data)
        }
    }
}