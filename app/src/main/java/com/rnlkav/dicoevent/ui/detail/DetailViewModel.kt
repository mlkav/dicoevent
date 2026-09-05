package com.rnlkav.dicoevent.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.rnlkav.dicoevent.data.EventRepository
import com.rnlkav.dicoevent.data.Result
import com.rnlkav.dicoevent.data.local.entity.FavoriteEvent
import com.rnlkav.dicoevent.data.response.ListEventsItem
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: EventRepository) : ViewModel() {

    private val _eventId = MutableLiveData<Int>()
    val eventDetail: LiveData<Result<ListEventsItem>> = _eventId.switchMap { id ->
        repository.getEventDetail(id)
    }

    fun getEventDetail(id: Int) {
        if (_eventId.value == null || _eventId.value != id) {
            _eventId.value = id
        }
    }

    fun refreshDetail() {
        _eventId.value?.let {
            _eventId.value = it
        }
    }

    fun getFavoriteById(id: String) = repository.getFavoriteById(id)

    fun setFavorite(event: FavoriteEvent, favoriteState: Boolean) {
        viewModelScope.launch {
            repository.setFavorite(event, favoriteState)
        }
    }
}
