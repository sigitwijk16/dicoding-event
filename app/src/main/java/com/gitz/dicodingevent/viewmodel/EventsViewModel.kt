package com.gitz.dicodingevent.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitz.dicodingevent.data.response.EventItem
import com.gitz.dicodingevent.data.retrofit.ApiConfig
import kotlinx.coroutines.launch

class EventsViewModel : ViewModel() {

    private val apiService = ApiConfig.getApiService()

    private val _events = MutableLiveData<List<EventItem>>()
    val events: LiveData<List<EventItem>> = _events

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadEvents(active: Int) {
        if (_events.value != null) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getEvents(active)
                _events.value = response.listEvents
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchEvents(keyword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.searchEvents(
                    active = -1,
                    keyword = keyword
                )
                _events.value = response.listEvents
            } finally {
                _isLoading.value = false
            }
        }
    }

}
