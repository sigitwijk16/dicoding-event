package com.gitz.dicodingevent.data.retrofit

import com.gitz.dicodingevent.data.response.EventDetailResponse
import com.gitz.dicodingevent.data.response.EventResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    suspend fun getEvents(
        @Query("active") active: Int
    ): EventResponse

    @GET("events")
    suspend fun searchEvents(
        @Query("active") active: Int = -1,
        @Query("q") keyword: String
    ): EventResponse

    @GET("events/{id}")
    suspend fun getEventDetail(
        @Path("id") id: String
    ): EventDetailResponse
}