package com.example.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

            // Save the updated list of events to local storage
            saveEventsToLocalStorage(updatedEvents)
        }

        eventListAdapter.setOnEditClickListener { event ->
            val editIntent = Intent(this, EditEventActivity::class.java)
            editIntent.putExtra("event_id", event.id)
            startActivityForResult(editIntent, EDIT_EVENT_REQUEST_CODE)
        }
    }

    // Refresh the list of events after an edit is made in the EditEventActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_EVENT_REQUEST_CODE && resultCode == RESULT_OK) {
            val savedEvents = getSavedEventsFromLocalStorage()
            eventListAdapter.setEvents(savedEvents)
            // acknowledge the user that the event was updated
            Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show()
        }
    }

    // Retrieve list of saved events from local storage
    private fun getSavedEventsFromLocalStorage(): List<Event> {
        val sharedPreferences = getSharedPreferences("MyEvents", Context.MODE_PRIVATE)
        val eventJsonString = sharedPreferences.getString("events", null) ?: return emptyList()

        // Convert the JSON string to a list of Event objects
        val eventListType = object : TypeToken<List<Event>>() {}.type
        val events = Gson().fromJson<List<Event>>(eventJsonString, eventListType)

        // Assign an id to each event based on its position in the list
        return events.mapIndexed { index, event ->
            event.copy(id = index + 1)
        }
    }

    // Save list of events to local storage
    private fun saveEventsToLocalStorage(events: List<Event>) {
        val sharedPreferences = getSharedPreferences("MyEvents", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Convert into json
        val eventListType = object : TypeToken<List<Event>>() {}.type
        val eventJsonString = Gson().toJson(events, eventListType)

        editor.putString("events", eventJsonString)
        editor.apply()
    }

    companion object {
        private const val EDIT_EVENT_REQUEST_CODE = 1
    }
}
