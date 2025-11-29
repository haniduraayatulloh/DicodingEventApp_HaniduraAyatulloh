package com.example.dicodingevent.data.model

import com.google.gson.annotations.SerializedName

data class EventItem(

    @field:SerializedName("id")
    val id: Int?,

    @field:SerializedName("name")
    val name: String?,
    @field:SerializedName("ownerName")
    val ownerName: String?,
    @field:SerializedName("beginTime")
    val beginTime: String?,
    @field:SerializedName("imageLogo")
    val imageLogo: String?,
    @field:SerializedName("mediaCover")
    val mediaCover: String?,
    @field:SerializedName("quota")
    val quota: Int?,
    @field:SerializedName("summary")
    val summary: String? = null,


    @field:SerializedName("registrants")
    val registered: Int?,

    @field:SerializedName("description")
    val description: String?,
    @field:SerializedName("link")
    val link: String?

) {

    fun getImageUrl(): String? {
        return mediaCover ?: imageLogo
    }


    fun getRemainingQuota(): Int? {
        return if (quota != null && registered != null) {
            quota - registered
        } else {
            null
        }
    }
}