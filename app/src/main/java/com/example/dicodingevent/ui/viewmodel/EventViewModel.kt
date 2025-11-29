package com.example.dicodingevent.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.model.EventItem
import com.example.dicodingevent.data.repository.EventRepository
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.IllegalArgumentException

sealed class EventResult<out T> {
    data class Success<out T>(val data: T?) : EventResult<T>()
    data class Error(val exception: Exception) : EventResult<Nothing>()
    object Loading : EventResult<Nothing>()
}

class EventViewModel(private val repository: EventRepository) : ViewModel() {


    private val _upcomingEvents = MutableLiveData<EventResult<List<EventItem>>>()
    val upcomingEvents: LiveData<EventResult<List<EventItem>>> = _upcomingEvents


    private val _finishedEvents = MutableLiveData<EventResult<List<EventItem>>>()
    val finishedEvents: LiveData<EventResult<List<EventItem>>> = _finishedEvents


    private val _detailEvent = MutableLiveData<EventResult<EventItem?>>()
    val detailEvent: LiveData<EventResult<EventItem?>> = _detailEvent


    private val _searchResults = MutableLiveData<EventResult<List<EventItem>>>()
    val searchResults: LiveData<EventResult<List<EventItem>>> = _searchResults

    init {

        fetchUpcomingEvents()
        fetchFinishedEvents()
    }

    fun fetchUpcomingEvents() {
        viewModelScope.launch {
            _upcomingEvents.value = EventResult.Loading
            try {
                val eventList = repository.getUpcomingEvents()
                _upcomingEvents.value = EventResult.Success(eventList)
            } catch (e: Exception) {
                _upcomingEvents.value = EventResult.Error(e)
            }
        }
    }

    fun fetchFinishedEvents() {
        viewModelScope.launch {
            _finishedEvents.value = EventResult.Loading
            try {
                val eventList = repository.getFinishedEvents()
                _finishedEvents.value = EventResult.Success(eventList)
            } catch (e: Exception) {
                _finishedEvents.value = EventResult.Error(e)
            }
        }
    }


    fun fetchDetailEvent(eventId: String) {
        viewModelScope.launch {
            _detailEvent.value = EventResult.Loading
            try {

                val idInt = eventId.toIntOrNull()

                if (idInt != null) {

                    val eventDetail = repository.getEventDetail(idInt)
                    _detailEvent.value = EventResult.Success(eventDetail)
                } else {

                    _detailEvent.value = EventResult.Error(IllegalArgumentException("ID Event tidak valid"))
                }
            } catch (e: Exception) {
                _detailEvent.value = EventResult.Error(e)
            }
        }
    }


    fun searchEvents(query: String) {
        viewModelScope.launch {
            _searchResults.value = EventResult.Loading
            try {
                val eventList = repository.searchEvents(query)
                _searchResults.value = EventResult.Success(eventList)
            } catch (e: Exception) {
                _searchResults.value = EventResult.Error(e)
            }
        }
    }
}