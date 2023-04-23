package com.example.main

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.util.Calendar

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

        // Find UI elements
        val eventNameEditText = findViewById<EditText>(R.id.eventNameEditText)
        val eventDescriptionEditText = findViewById<EditText>(R.id.eventDescriptionEditText)
        val eventLocationEditText = findViewById<EditText>(R.id.eventLocationEditText)
        val eventDataIcon = findViewById<ImageView>(R.id.event_date_edit_icon)
        val eventTimeIcon = findViewById<ImageView>(R.id.event_time_edit_icon)
        val eventDateEditText = findViewById<EditText>(R.id.eventDateEditText)
        val eventTimeEditText = findViewById<EditText>(R.id.eventTimeEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)

        // Populate the UI elements with the data of the selected event
        eventNameEditText.setText(event.name)
        eventDescriptionEditText.setText(event.description)
        eventLocationEditText.setText(event.location)
        eventDateEditText.setText(event.date)
        eventTimeEditText.setText(event.time)

        // Set click listener for event date edit text
        eventDataIcon.setOnClickListener {
//            showDatePickerDialog()
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = "${monthOfYear + 1}/${dayOfMonth}/${year}"
                eventDateEditText.setText(selectedDate)
            }, year, month, day)

            // Set minimum date to today's date
            datePicker.datePicker.minDate = System.currentTimeMillis() - 1000

            datePicker.show()
        }

        // Set click listener for event time edit text
        eventTimeIcon.setOnClickListener {
//            showTimePickerDialog()
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(this, { _, hourOfDay, minute ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedCalendar.set(Calendar.MINUTE, minute)
                selectedCalendar.set(Calendar.SECOND, 0)
                selectedCalendar.set(Calendar.MILLISECOND, 0)

                if (selectedCalendar.timeInMillis < calendar.timeInMillis) {
                    Toast.makeText(this, "Please select a time in the future", Toast.LENGTH_SHORT).show()
                    eventTimeEditText.setText("")
                } else {
                    val amPm = if (hourOfDay < 12) "AM" else "PM"
                    val selectedTime = String.format("%02d:%02d %s", if (hourOfDay > 12) hourOfDay - 12 else hourOfDay, minute, amPm)
                    eventTimeEditText.setText(selectedTime)
                }
            }, hour, minute, false)

            timePicker.show()
        }

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

            // Notify the adapter
            setResult(Activity.RESULT_OK)
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

//         Assign an id to each event based on its position in the list
        return events.mapIndexed { index, event ->
            event.copy(id = index + 1)
        }
    }

    // Save list of events to local storage
    private fun saveEventsToLocalStorage(events:
                                         List<Event>) {
        val sharedPreferences = getSharedPreferences("MyEvents", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Convert the list of events to a JSON string
        val eventJsonString = Gson().toJson(events)

        // Save the JSON string to local storage
        editor.putString("events", eventJsonString)
        editor.apply()
    }
}

