package com.example.main

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TimePicker
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateEventActivity : AppCompatActivity() {

    private lateinit var eventNameEditText: EditText
    private lateinit var eventDescriptionEditText: EditText
    private lateinit var eventLocationEditText: EditText
    private lateinit var eventDateEditText: EditText
    private lateinit var eventTimeEditText: EditText
    private lateinit var createEventButton: Button
    private lateinit var eventDateIcon : ImageView
    private lateinit var eventTimeIcon : ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        // Initialize UI elements
        eventNameEditText = findViewById(R.id.event_name_edit_text)
        eventDescriptionEditText = findViewById(R.id.event_description_edit_text)
        eventLocationEditText = findViewById(R.id.event_location_edit_text)
        eventDateIcon = findViewById(R.id.event_date_create_icon)
        eventDateEditText = findViewById(R.id.eventDateCreateText)
        eventTimeIcon = findViewById(R.id.event_time_create_icon)
        eventTimeEditText = findViewById(R.id.eventTimeCreateText)

        createEventButton = findViewById(R.id.submit_button)

        // Set listener for date picker
        eventDateIcon.setOnClickListener {
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

        // Set listener for time picker
        eventTimeIcon.setOnClickListener {
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
            val newEvent = Event( 1,eventName, eventDescription, eventLocation, eventDate, eventTime, R.drawable.science_fair)

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
