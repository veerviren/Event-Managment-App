package com.example.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ViewEventsActivity : AppCompatActivity() {

    private lateinit var eventListAdapter: EventListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_events)

        // Set up RecyclerView
        val eventRecyclerView = findViewById<RecyclerView>(R.id.eventRecyclerView)
        eventRecyclerView.layoutManager = LinearLayoutManager(this)
        eventListAdapter = EventListAdapter()
        eventRecyclerView.adapter = eventListAdapter

        // Retrieve list of saved events and update adapter
        val savedEvents = getSavedEventsFromLocalStorage()
        eventListAdapter.setEvents(savedEvents)

        // Set click listeners for delete and edit buttons
        eventListAdapter.setOnDeleteClickListener { event ->
            // Remove the event from the list
            val updatedEvents = eventListAdapter.getEvents().toMutableList()
            updatedEvents.remove(event)
            eventListAdapter.setEvents(updatedEvents)

            // acknowledge the user that the event was deleted
            Toast.makeText(this, "Event deleted successfully", Toast.LENGTH_SHORT).show()
        }

        eventListAdapter.setOnEditClickListener { event ->
            val editIntent = Intent(this, EditEventActivity::class.java)
            editIntent.putExtra("event_id", event.id)
            startActivity(editIntent)
        }
    }


    // Retrieve list of saved events from local storage
    private fun getSavedEventsFromLocalStorage(): List<Event> {
        val sharedPreferences = getSharedPreferences("MyEvents", Context.MODE_PRIVATE)
        val eventJsonString = sharedPreferences.getString("events", null)

        // If there are no saved events, return an empty list
        if (eventJsonString == null) {
            return emptyList()
        }

        // Convert the JSON string to a list of Event objects
        val eventListType = object : TypeToken<List<Event>>() {}.type
        val events = Gson().fromJson<List<Event>>(eventJsonString, eventListType)

        // Assign an id to each event based on its position in the list
        return events.mapIndexed { index, event ->
            event.copy(id = index + 1)
        }
    }
}


