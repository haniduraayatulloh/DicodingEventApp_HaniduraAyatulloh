package com.example.dicodingevent.di

import com.example.dicodingevent.data.repository.EventRepository
import com.example.dicodingevent.network.ApiConfig

object Injection {

    fun provideRepository(): EventRepository {
        val apiService = ApiConfig.getApiService()
        return EventRepository.getInstance(apiService)
    }
}