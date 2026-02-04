package com.gitz.dicodingevent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkManager
import com.gitz.dicodingevent.data.EventRepository
import com.gitz.dicodingevent.data.local.pref.SettingPreferences
import com.gitz.dicodingevent.viewmodel.event.EventViewModel
import com.gitz.dicodingevent.viewmodel.eventdetail.EventDetailViewModel
import com.gitz.dicodingevent.viewmodel.home.HomeViewModel
import com.gitz.dicodingevent.viewmodel.setting.SettingViewModel

class ViewModelFactory private constructor(
    private val repository: EventRepository,
    private val pref: SettingPreferences,
    private val workManager: WorkManager
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(EventViewModel::class.java) -> {
                EventViewModel(repository) as T
            }
            modelClass.isAssignableFrom(EventDetailViewModel::class.java) -> {
                EventDetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                SettingViewModel(pref, workManager) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(
            repository: EventRepository,
            pref: SettingPreferences,
            workManager: WorkManager
        ): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(repository, pref, workManager)
            }.also { instance = it }
    }
}