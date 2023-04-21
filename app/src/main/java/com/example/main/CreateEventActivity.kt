package com.example.main

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CreateEventActivity : AppCompatActivity() {

    private lateinit var eventNameEditText: EditText
    private lateinit var eventDescriptionEditText: EditText
    private lateinit var eventLocationEditText: EditText
    private lateinit var eventDateEditText: EditText
    private lateinit var eventTimeEditText: EditText
    private lateinit var createEventButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        // Initialize UI elements
        eventNameEditText = findViewById(R.id.event_name_edit_text)
        eventDescriptionEditText = findViewById(R.id.event_description_edit_text)
        eventLocationEditText = findViewById(R.id.event_location_edit_text)
        eventDateEditText = findViewById(R.id.event_date_edit_text)
        eventTimeEditText = findViewById(R.id.event_time_edit_text)
        createEventButton = findViewById(R.id.submit_button)

        // Set click listener for create event button
        createEventButton.setOnClickListener {
            // Retrieve entered event details
            val eventName = eventNameEditText.text.toString()
            val eventDescription = eventDescriptionEditText.text.toString()
            val eventLocation = eventLocationEditText.text.toString()
            val eventDate = eventDateEditText.text.toString()
            val eventTime = eventTimeEditText.text.toString()

            // Validate entered event details
            if (eventName.isEmpty() || eventDescription.isEmpty() || eventLocation.isEmpty() || eventDate.isEmpty() || eventTime.isEmpty()) {
                Toast.makeText(this, "Please fill out all event details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create new event
            val newEvent = Event(1,eventName, eventDescription, eventLocation, eventDate, eventTime)

            // Save new event to shared preferences
            val sharedPreferences = getSharedPreferences("MyEvents", Context.MODE_PRIVATE)
            val eventListType = object : TypeToken<List<Event>>() {}.type
            val eventJsonString = sharedPreferences.getString("events", null)
            val eventList = if (eventJsonString == null) {
                mutableListOf(newEvent)
            } else {
                Gson().fromJson(eventJsonString, eventListType) as MutableList<Event>
            }
            eventList.add(newEvent)
            sharedPreferences.edit().putString("events", Gson().toJson(eventList)).apply()

            // Display success message
            Toast.makeText(this, "Event created successfully", Toast.LENGTH_SHORT).show()

            // Finish activity
            finish()
        }
    }
}

