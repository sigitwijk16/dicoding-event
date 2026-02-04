package com.gitz.dicodingevent.data.remote.response

import com.google.gson.annotations.SerializedName

data class EventDetailResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("event")
	val event: EventItem
)