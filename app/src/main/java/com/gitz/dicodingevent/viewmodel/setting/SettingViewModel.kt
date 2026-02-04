package com.gitz.dicodingevent.viewmodel.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.gitz.dicodingevent.DailyEventWorker
import com.gitz.dicodingevent.data.local.pref.SettingPreferences
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SettingViewModel(private val pref: SettingPreferences, private val workManager: WorkManager) : ViewModel() {

    fun getThemeSettings(): LiveData<Boolean> = pref.getThemeSetting().asLiveData()
    fun getReminderSettings(): LiveData<Boolean> = pref.getReminderSetting().asLiveData()

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch { pref.saveThemeSetting(isDarkModeActive) }
    }

    fun saveReminderSetting(isActive: Boolean) {
        viewModelScope.launch {
            pref.saveReminderSetting(isActive)
            if (isActive) {
                startDailyReminder()
            } else {
                cancelDailyReminder()
            }
        }
    }

    private fun startDailyReminder() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyEventWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .addTag("DailyReminderTag")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "DailyReminderWork",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }

    private fun cancelDailyReminder() {
        workManager.cancelUniqueWork("DailyReminderWork")
    }
}