package com.gitz.dicodingevent.viewmodel.event

import androidx.lifecycle.ViewModel
import com.gitz.dicodingevent.data.EventRepository

class EventViewModel(private val repository: EventRepository) : ViewModel() {
    fun loadEvents(active: Int) = repository.getEvents(active)
    fun searchEvents(keyword: String) = repository.searchEvents(keyword)
    fun getFavoriteEvents() = repository.getFavoriteEvents()
}