package com.example.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class EditEventActivity : AppCompatActivity() {

    private lateinit var event: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        // Retrieve the ID of the event that the user wants to edit
        val eventId = intent.getIntExtra("event_id", -1)

        // Retrieve the list of saved events and find the event with the matching ID
        val savedEvents = getSavedEventsFromLocalStorage()
        event = savedEvents.find { it.id == eventId } ?: return

        val eventNameEditText = findViewById<EditText>(R.id.eventNameEditText)
        val eventDescriptionEditText = findViewById<EditText>(R.id.eventDescriptionEditText)
        val eventLocationEditText = findViewById<EditText>(R.id.eventLocationEditText)
        val eventDateEditText = findViewById<EditText>(R.id.eventDateEditText)
        val eventTimeEditText = findViewById<EditText>(R.id.eventTimeEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)

        // Populate the UI elements with the data of the selected event
        eventNameEditText.setText(event.name)
        eventDescriptionEditText.setText(event.description)
        eventLocationEditText.setText(event.location)
        eventDateEditText.setText(event.date)
        eventTimeEditText.setText(event.time)

        // Set click listener for save button
        saveButton.setOnClickListener {
            // Update the event object with the new values from the UI elements
            event = event.copy(
                name = eventNameEditText.text.toString(),
                description = eventDescriptionEditText.text.toString(),
                location = eventLocationEditText.text.toString(),
                date = eventDateEditText.text.toString(),
                time = eventTimeEditText.text.toString()
            )

            // Retrieve the list of saved events and update the corresponding event
            val updatedEvents = getSavedEventsFromLocalStorage().toMutableList()
            val index = updatedEvents.indexOfFirst { it.id == event.id }
            if (index != -1) {
                updatedEvents[index] = event
            }

            // Save the updated list of events to local storage
            saveEventsToLocalStorage(updatedEvents)

            // Notify the adapter that the data has changed
            val intent = Intent().apply {
                putExtra("event_id", event.id)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    // Retrieve list of saved events from local storage
    private fun getSavedEventsFromLocalStorage(): List<Event> {
        val sharedPreferences = getSharedPreferences("MyEvents", Context.MODE_PRIVATE)
        val eventJsonString = sharedPreferences.getString("events", null) ?: return emptyList()

        // If there are no saved events, return an empty list

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
}
