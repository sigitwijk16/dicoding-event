package com.gitz.dicodingevent.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitz.dicodingevent.data.response.EventItem
import com.gitz.dicodingevent.data.retrofit.ApiConfig
import com.gitz.dicodingevent.utils.Event
import kotlinx.coroutines.launch

class EventDetailViewModel : ViewModel() {

    private val apiService = ApiConfig.getApiService()

    private val _event = MutableLiveData<EventItem>()
    val event: LiveData<EventItem> = _event

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<Event<String>>()
    val error: LiveData<Event<String>> = _error

    fun loadDetail(id: Int) {
        if (_event.value != null) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                _event.value = apiService.getEventDetail(id.toString()).event
            } catch (e: Exception) {
                _error.value = Event(e.message ?: "Unknown error occurred")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
