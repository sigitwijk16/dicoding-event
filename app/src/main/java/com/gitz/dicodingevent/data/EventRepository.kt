package com.gitz.dicodingevent.data

import com.gitz.dicodingevent.data.remote.retrofit.ApiService
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.gitz.dicodingevent.data.local.HomeEvents
import com.gitz.dicodingevent.data.local.entity.FavoriteEvent
import com.gitz.dicodingevent.data.local.room.FavoriteEventDao
import com.gitz.dicodingevent.data.remote.response.EventItem

class EventRepository private constructor(
    private val apiService: ApiService,
    private val favoriteEventDao: FavoriteEventDao
) {

    fun getEvents(active: Int): LiveData<Result<List<EventItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getEvents(active)
            val eventList = response.listEvents
            emit(Result.Success(eventList))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An error occurred"))
        }
    }

    fun getCombinedHomeEvents(): LiveData<Result<HomeEvents>> = liveData {
        emit(Result.Loading)
        try {
            val activeResponse = apiService.getEvents(1)
            val inactiveResponse = apiService.getEvents(0)

            val combinedData = HomeEvents(
                activeEvents = activeResponse.listEvents.take(5),
                inactiveEvents = inactiveResponse.listEvents.take(5)
            )

            emit(Result.Success(combinedData))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Failed to load home content"))
        }
    }
    fun searchEvents(keyword: String): LiveData<Result<List<EventItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.searchEvents(-1, keyword)
            emit(Result.Success(response.listEvents))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Search failed"))
        }
    }

    fun getEventDetail(id: Int): LiveData<Result<EventItem>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getEventDetail(id.toString())
            emit(Result.Success(response.event))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Detail not found"))
        }
    }

    fun getFavoriteEvents(): LiveData<List<FavoriteEvent>> {
        return favoriteEventDao.getAllFavoriteEvents()
    }

    fun isFavorite(id: Int): LiveData<FavoriteEvent?> {
        return favoriteEventDao.getFavoriteEventById(id)
    }

    suspend fun setFavorite(event: EventItem, bookmarkState: Boolean) {
        val favoriteEvent = FavoriteEvent(event.id, event.name, event.mediaCover)
        if (bookmarkState) {
            favoriteEventDao.insertFavorite(favoriteEvent)
        } else {
            favoriteEventDao.deleteFavorite(favoriteEvent)
        }
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
            favoriteEventDao: FavoriteEventDao
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, favoriteEventDao)
            }.also { instance = it }
    }
}