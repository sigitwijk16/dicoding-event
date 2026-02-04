package com.gitz.dicodingevent.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gitz.dicodingevent.data.EventRepository
import com.gitz.dicodingevent.data.Result
import com.gitz.dicodingevent.data.local.HomeEvents

class HomeViewModel(private val repository: EventRepository) : ViewModel() {
    val homeData: LiveData<Result<HomeEvents>> = repository.getCombinedHomeEvents()
}