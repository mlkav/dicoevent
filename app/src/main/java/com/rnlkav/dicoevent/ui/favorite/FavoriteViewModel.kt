package com.rnlkav.dicoevent.ui.favorite

import androidx.lifecycle.ViewModel
import com.rnlkav.dicoevent.data.EventRepository

class FavoriteViewModel(private val repository: EventRepository) : ViewModel() {
    fun getAllFavorite() = repository.getAllFavorite()
}
