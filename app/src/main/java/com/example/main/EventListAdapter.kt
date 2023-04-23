package com.example.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventListAdapter : RecyclerView.Adapter<EventListAdapter.ViewHolder>() {
    private var events: List<Event> = emptyList()
    private var onDeleteClickListener: ((Event) -> Unit)? = null
    private var onEditClickListener: ((Event) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.event_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        holder.nameTextView.text = event.name
        holder.timeTextView.text = event.time
        holder.dateTextView.text = event.date
        holder.locationTextView.text = event.location

        // Set click listeners for delete and edit buttons
        holder.deleteButton.setOnClickListener {
            onDeleteClickListener?.invoke(event)
        }

        holder.editButton.setOnClickListener {
            onEditClickListener?.invoke(event)
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setEvents(events: List<Event>) {
        this.events = events
        notifyDataSetChanged()
    }

    fun setOnDeleteClickListener(listener: (Event) -> Unit) {
        this.onDeleteClickListener = listener
    }

    fun setOnEditClickListener(listener: (Event) -> Unit) {
        this.onEditClickListener = listener
    }

    fun getEvents(): List<Event> {
        return events
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.eventNameTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.eventDateTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.eventTimeTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.eventLocationTextView)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        val editButton: Button = itemView.findViewById(R.id.editButton)
    }
}
