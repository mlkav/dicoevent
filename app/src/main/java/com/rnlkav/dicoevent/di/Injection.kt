package com.rnlkav.dicoevent.di

import android.content.Context
import com.rnlkav.dicoevent.data.EventRepository
import com.rnlkav.dicoevent.data.local.datastore.SettingPreferences
import com.rnlkav.dicoevent.data.local.datastore.dataStore
import com.rnlkav.dicoevent.data.local.room.EventDatabase
import com.rnlkav.dicoevent.data.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val dao = database.favoriteDao()
        return EventRepository.getInstance(apiService, dao)
    }

    fun provideSettingPreferences(context: Context): SettingPreferences {
        return SettingPreferences.getInstance(context.dataStore)
    }
}
