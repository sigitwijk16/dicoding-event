package com.gitz.dicodingevent.di

import android.content.Context
import androidx.work.WorkManager
import com.gitz.dicodingevent.data.EventRepository
import com.gitz.dicodingevent.data.local.pref.SettingPreferences
import com.gitz.dicodingevent.data.local.pref.dataStore
import com.gitz.dicodingevent.data.local.room.EventDatabase
import com.gitz.dicodingevent.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService(context)
        val database = EventDatabase.getInstance(context)
        val dao = database.favoriteEventDao()
        return EventRepository.getInstance(apiService, dao)
    }

    fun provideSettingPreferences(context: Context): SettingPreferences {
        return SettingPreferences.getInstance(context.dataStore)
    }

    fun provideWorkManager(context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}