package com.example.dicodingevent.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingevent.data.repository.EventRepository
import com.example.dicodingevent.ui.viewmodel.EventViewModel

class ViewModelFactory private constructor(
    private val eventRepository: EventRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(EventViewModel::class.java) -> {
                EventViewModel(eventRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(

                    Injection.provideRepository()
                ).also { instance = it }
            }
    }
}