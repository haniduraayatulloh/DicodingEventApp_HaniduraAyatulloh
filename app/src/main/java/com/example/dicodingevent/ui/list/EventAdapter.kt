package com.example.dicodingevent.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.dicodingevent.R
import com.example.dicodingevent.data.model.EventItem
import com.example.dicodingevent.databinding.ItemEventBinding
import java.text.SimpleDateFormat
import java.util.Locale

class EventAdapter(
    private val onClickListener: (EventItem) -> Unit,

    private val statusText: String
) : ListAdapter<EventItem, EventAdapter.EventViewHolder>(EventDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
        holder.itemView.setOnClickListener {
            onClickListener(event)
        }
    }

    inner class EventViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: EventItem) {
            binding.apply {

                tvEventName.text = event.name ?: itemView.context.getString(R.string.not_available)

                val time = event.beginTime

                tvEventTime.text = formatTime(time)


                tvEventStatus.text = statusText

                event.getImageUrl()?.let { url ->
                    ivEventImage.load(url) {
                        crossfade(true)
                        placeholder(R.drawable.ic_image_placeholder)
                        error(R.drawable.ic_image_placeholder)
                    }
                } ?: ivEventImage.setImageResource(R.drawable.ic_image_placeholder)
            }
        }


        private fun formatTime(time: String?): String {
            return try {
                if (time.isNullOrEmpty()) return itemView.context.getString(R.string.time_unknown)


                val apiFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())


                val displayFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale("in", "ID"))

                val date = apiFormat.parse(time)
                if (date != null) {
                    displayFormat.format(date)
                } else {
                    itemView.context.getString(R.string.time_unknown)
                }
            } catch (e: Exception) {
                itemView.context.getString(R.string.time_unknown)
            }
        }
    }
}