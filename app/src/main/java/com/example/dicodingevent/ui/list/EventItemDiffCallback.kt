package com.example.dicodingevent.ui.list

import androidx.recyclerview.widget.DiffUtil
import com.example.dicodingevent.data.model.EventItem

class EventItemDiffCallback : DiffUtil.ItemCallback<EventItem>() {


    override fun areItemsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {
        return oldItem.id == newItem.id
    }


    override fun areContentsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {

        return oldItem == newItem
    }
}