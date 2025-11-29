package com.example.dicodingevent.network

import com.example.dicodingevent.data.model.EventResponse
import com.example.dicodingevent.data.model.EventDetailWrapperResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {


    @GET("events")
    suspend fun getUpcomingEvents(
        @Query("active") active: Int = 1,
        @Query("limit") limit: Int = 40
    ): EventResponse


    @GET("events")
    suspend fun getFinishedEvents(
        @Query("active") active: Int = 0,
        @Query("limit") limit: Int = 40
    ): EventResponse


    @GET("events/{id}")
    suspend fun getEventDetailResponse(@Path("id") id: Int): EventDetailWrapperResponse
}