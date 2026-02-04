package com.gitz.dicodingevent.viewmodel.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.gitz.dicodingevent.data.EventRepository
import com.gitz.dicodingevent.data.Result
import com.gitz.dicodingevent.data.remote.response.EventItem

class EventViewModel(private val repository: EventRepository) : ViewModel() {
    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    val searchResult = _searchQuery.switchMap { query ->
        repository.searchEvents(query)
    }

    fun setSearchQuery(query: String) {
        if (_searchQuery.value == query) return
        _searchQuery.value = query
    }

    fun loadEvents(active: Int) = repository.getEvents(active)
    fun searchEvents(keyword: String) = repository.searchEvents(keyword)
    fun getFavoriteEvents() = repository.getFavoriteEvents()
}