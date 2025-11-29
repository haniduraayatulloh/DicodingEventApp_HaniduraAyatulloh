package com.example.dicodingevent.data.repository

import com.example.dicodingevent.data.model.EventItem
import com.example.dicodingevent.network.ApiService
import retrofit2.HttpException
import java.lang.Exception
import android.util.Log

class EventRepository private constructor(private val apiService: ApiService) {

    suspend fun getUpcomingEvents(): List<EventItem> {
        return try {
            val response = apiService.getUpcomingEvents(active = 1)
            response.listEvents?.filterNotNull() ?: emptyList()

        } catch (e: Exception) {
            Log.e("Repository", "Error fetching upcoming events: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getFinishedEvents(): List<EventItem> {
        return try {
            val response = apiService.getFinishedEvents(active = 0)
            response.listEvents?.filterNotNull() ?: emptyList()

        } catch (e: Exception) {
            Log.e("Repository", "Error fetching finished events: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getEventDetail(id: Int): EventItem? {
        return try {
            val response = apiService.getEventDetailResponse(id)

            if (response.error == false) {
                return response.event
            } else {
                Log.e("Repository", "Error fetching detail: ${response.message}")
                return null
            }
        } catch (e: HttpException) {
            Log.e("Repository", "HTTP Error detail: ${e.message}")
            e.printStackTrace()
            return null
        } catch (e: Exception) {
            Log.e("Repository", "General Error detail: ${e.message}")
            e.printStackTrace()
            return null
        }
    }

    suspend fun searchEvents(query: String): List<EventItem> {
        Log.w("Repository", "Search function is not fully implemented on API side.")
        return emptyList()
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null

        fun getInstance(apiService: ApiService): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService).also { instance = it }
            }
    }
}