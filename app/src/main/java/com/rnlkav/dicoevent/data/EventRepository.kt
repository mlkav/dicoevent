package com.rnlkav.dicoevent.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.rnlkav.dicoevent.data.local.entity.FavoriteEvent
import com.rnlkav.dicoevent.data.local.room.FavoriteDao
import com.rnlkav.dicoevent.data.response.ListEventsItem
import com.rnlkav.dicoevent.data.retrofit.ApiService
import kotlinx.coroutines.Dispatchers

class EventRepository private constructor(
    private val apiService: ApiService,
    private val favoriteDao: FavoriteDao,
) {
    fun getEvents(active: Int, q: String? = null, limit: Int = 40): LiveData<Result<List<ListEventsItem>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.getEvents(active, q, limit)
            emit(Result.Success(response.listEvents))
        } catch (e: Exception) {
            val errorMessage = if (e.message?.contains("Unable to resolve host") == true) {
                "Koneksi internet terputus. Silakan periksa jaringan Anda."
            } else {
                e.message.toString()
            }
            emit(Result.Error(errorMessage))
        }
    }

    fun getEventDetail(id: Int): LiveData<Result<ListEventsItem>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.getEventDetail(id)
            emit(Result.Success(response.event))
        } catch (e: Exception) {
            val errorMessage = if (e.message?.contains("Unable to resolve host") == true) {
                "Koneksi internet terputus. Silakan periksa jaringan Anda."
            } else {
                e.message.toString()
            }
            emit(Result.Error(errorMessage))
        }
    }

    fun getAllFavorite(): LiveData<List<FavoriteEvent>> = favoriteDao.getAllFavorite()

    fun getFavoriteById(id: String): LiveData<FavoriteEvent> = favoriteDao.getFavoriteById(id)

    suspend fun setFavorite(event: FavoriteEvent, favoriteState: Boolean) {
        if (favoriteState) {
            favoriteDao.insert(event)
        } else {
            favoriteDao.delete(event)
        }
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
            favoriteDao: FavoriteDao,
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, favoriteDao)
            }.also { instance = it }
    }
}
