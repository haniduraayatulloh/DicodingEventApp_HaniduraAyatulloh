package com.example.dicodingevent.data.model

import com.google.gson.annotations.SerializedName

data class EventDetailWrapperResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("event")
    val event: EventItem
)