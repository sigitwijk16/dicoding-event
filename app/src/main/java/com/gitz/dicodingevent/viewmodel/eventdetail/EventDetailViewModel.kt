package com.gitz.dicodingevent.viewmodel.eventdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitz.dicodingevent.data.EventRepository
import com.gitz.dicodingevent.data.remote.response.EventItem
import kotlinx.coroutines.launch

class EventDetailViewModel(private val repository: EventRepository) : ViewModel() {
    fun loadDetail(id: Int) = repository.getEventDetail(id)

    fun isFavorite(id: Int) = repository.isFavorite(id)

    fun setFavorite(event: EventItem, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.setFavorite(event, isFavorite)
        }
    }
}