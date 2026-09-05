package com.rnlkav.dicoevent.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.rnlkav.dicoevent.data.EventRepository
import com.rnlkav.dicoevent.data.Result
import com.rnlkav.dicoevent.data.response.ListEventsItem

class MainViewModel(private val repository: EventRepository) : ViewModel() {

    private val _upcomingLimit = MutableLiveData<Int>()
    val upcomingEvents: LiveData<Result<List<ListEventsItem>>> = _upcomingLimit.switchMap { limit ->
        repository.getEvents(active = 1, limit = limit)
    }

    private val _finishedLimit = MutableLiveData<Int>()
    val finishedEvents: LiveData<Result<List<ListEventsItem>>> = _finishedLimit.switchMap { limit ->
        repository.getEvents(active = 0, limit = limit)
    }

    private val _searchQuery = MutableLiveData<String>()
    val searchResults: LiveData<Result<List<ListEventsItem>>> = _searchQuery.switchMap { query ->
        repository.getEvents(active = -1, q = query)
    }

    fun getUpcomingEvents(limit: Int = 40) {
        if ((_upcomingLimit.value == null) || (_upcomingLimit.value != limit)) {
            _upcomingLimit.value = limit
        }
    }

    fun refreshUpcomingEvents(limit: Int = 40) {
        _upcomingLimit.value = limit
    }

    fun getFinishedEvents(limit: Int = 40) {
        if ((_finishedLimit.value == null) || (_finishedLimit.value != limit)) {
            _finishedLimit.value = limit
        }
    }

    fun refreshFinishedEvents(limit: Int = 40) {
        _finishedLimit.value = limit
    }

    fun searchEvents(query: String) {
        if (_searchQuery.value != query) {
            _searchQuery.value = query
        }
    }

    fun refreshSearch() {
        _searchQuery.value?.let {
            _searchQuery.value = it
        }
    }
}
