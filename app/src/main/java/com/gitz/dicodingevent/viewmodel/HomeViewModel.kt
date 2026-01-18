package com.gitz.dicodingevent.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitz.dicodingevent.data.response.EventItem
import com.gitz.dicodingevent.data.retrofit.ApiConfig
import com.gitz.dicodingevent.utils.Event
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val apiService = ApiConfig.getApiService()

    private val _activeEvents = MutableLiveData<List<EventItem>>()
    val activeEvents: LiveData<List<EventItem>> = _activeEvents

    private val _inactiveEvents = MutableLiveData<List<EventItem>>()
    val inactiveEvents: LiveData<List<EventItem>> = _inactiveEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<Event<String>>()
    val error: LiveData<Event<String>> = _error

    fun loadHomeEvents() {
        if (_activeEvents.value != null && _inactiveEvents.value != null) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val activeResponse = apiService.getEvents(1)
                _activeEvents.value = activeResponse.listEvents.take(5)

                val inactiveResponse = apiService.getEvents(0)
                _inactiveEvents.value = inactiveResponse.listEvents.take(5)

            } catch (e: Exception) {
                _error.value = Event(e.message ?: "Failed to load home events")
                Log.e("HomeViewModel", "Failed to load home events", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
