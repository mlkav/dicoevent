package com.rnlkav.dicoevent.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rnlkav.dicoevent.data.EventRepository
import com.rnlkav.dicoevent.data.local.datastore.SettingPreferences
import com.rnlkav.dicoevent.di.Injection
import com.rnlkav.dicoevent.ui.detail.DetailViewModel
import com.rnlkav.dicoevent.ui.favorite.FavoriteViewModel
import com.rnlkav.dicoevent.ui.setting.SettingViewModel

class ViewModelFactory private constructor(
    private val repository: EventRepository,
    private val pref: SettingPreferences,
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(FavoriteViewModel::class.java) -> {
                FavoriteViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                SettingViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideRepository(context),
                    Injection.provideSettingPreferences(context)
                )
            }.also { instance = it }
    }
}
