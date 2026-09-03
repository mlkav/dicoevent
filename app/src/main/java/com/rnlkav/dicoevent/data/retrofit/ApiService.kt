package com.rnlkav.dicoevent.data.retrofit

import com.rnlkav.dicoevent.data.response.DetailEventResponse
import com.rnlkav.dicoevent.data.response.EventResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("events")
    fun getEvents(
        @Query("active") active: Int,
        @Query("q") q: String? = null,
        @Query("limit") limit: Int? = 40
    ): Call<EventResponse>

    @GET("events/{id}")
    fun getEventDetail(
        @Path("id") id: Int
    ): Call<DetailEventResponse>
}
