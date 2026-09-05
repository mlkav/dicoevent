package com.rnlkav.dicoevent.data.retrofit

import com.rnlkav.dicoevent.data.response.DetailEventResponse
import com.rnlkav.dicoevent.data.response.EventResponse
import retrofit2.http.*

interface ApiService {
    @GET("events")
    suspend fun getEvents(
        @Query("active") active: Int,
        @Query("q") q: String? = null,
        @Query("limit") limit: Int? = 40
    ): EventResponse

    @GET("events/{id}")
    suspend fun getEventDetail(
        @Path("id") id: Int
    ): DetailEventResponse
}
