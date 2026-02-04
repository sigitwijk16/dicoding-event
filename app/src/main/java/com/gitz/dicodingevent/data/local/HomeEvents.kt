package com.gitz.dicodingevent.data.local

import com.gitz.dicodingevent.data.remote.response.EventItem

data class HomeEvents(
    val activeEvents: List<EventItem>,
    val inactiveEvents: List<EventItem>
)