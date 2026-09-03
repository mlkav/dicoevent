package com.rnlkav.dicoevent.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rnlkav.dicoevent.data.response.DetailEventResponse
import com.rnlkav.dicoevent.data.response.EventResponse
import com.rnlkav.dicoevent.data.response.ListEventsItem
import com.rnlkav.dicoevent.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    private val _upcomingEvents = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvents: LiveData<List<ListEventsItem>> = _upcomingEvents

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _eventDetail = MutableLiveData<ListEventsItem>()
    val eventDetail: LiveData<ListEventsItem> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun getUpcomingEvents(limit: Int = 40) {
        _isLoading.value = true
        _errorMessage.value = null
        val client = ApiConfig.getApiService().getEvents(active = 1, limit = limit)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _upcomingEvents.value = response.body()?.listEvents
                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = if (t.message?.contains("Unable to resolve host") == true) {
                    "Koneksi internet terputus. Silakan periksa jaringan Anda."
                } else {
                    t.message
                }
            }
        })
    }

    fun getFinishedEvents(limit: Int = 40) {
        _isLoading.value = true
        _errorMessage.value = null
        val client = ApiConfig.getApiService().getEvents(active = 0, limit = limit)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _finishedEvents.value = response.body()?.listEvents
                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = if (t.message?.contains("Unable to resolve host") == true) {
                    "Koneksi internet terputus. Silakan periksa jaringan Anda."
                } else {
                    t.message
                }
            }
        })
    }

    fun searchEvents(query: String) {
        _isLoading.value = true
        _errorMessage.value = null
        val client = ApiConfig.getApiService().getEvents(active = -1, q = query)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _finishedEvents.value = response.body()?.listEvents
                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = if (t.message?.contains("Unable to resolve host") == true) {
                    "Koneksi internet terputus. Silakan periksa jaringan Anda."
                } else {
                    t.message
                }
            }
        })
    }

    fun getEventDetail(id: Int) {
        _isLoading.value = true
        _errorMessage.value = null
        val client = ApiConfig.getApiService().getEventDetail(id)
        client.enqueue(object : Callback<DetailEventResponse> {
            override fun onResponse(call: Call<DetailEventResponse>, response: Response<DetailEventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _eventDetail.value = response.body()?.event
                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = if (t.message?.contains("Unable to resolve host") == true) {
                    "Koneksi internet terputus. Silakan periksa jaringan Anda."
                } else {
                    t.message
                }
            }
        })
    }
}
