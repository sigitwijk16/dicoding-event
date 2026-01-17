package com.gitz.dicodingevent.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitz.dicodingevent.data.response.EventItem
import com.gitz.dicodingevent.data.retrofit.ApiConfig
import kotlinx.coroutines.launch

class EventDetailViewModel : ViewModel() {

    private val apiService = ApiConfig.getApiService()

    private val _event = MutableLiveData<EventItem>()
    val event: LiveData<EventItem> = _event

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadDetail(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _event.value = apiService.getEventDetail(id.toString()).event
            } finally {
                _isLoading.value = false
            }
        }
    }
}
