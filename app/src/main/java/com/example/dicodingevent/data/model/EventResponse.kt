package com.example.dicodingevent.data.model

import com.google.gson.annotations.SerializedName

data class EventResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,


    @field:SerializedName("listEvents")
    val listEvents: List<EventItem?>? = null
)