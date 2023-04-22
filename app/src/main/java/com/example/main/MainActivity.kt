package com.example.main

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private val IMAGE_PICKER_REQUEST_CODE = 123

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val upcomingEvents = findViewById<LinearLayout>(R.id.upcoming_events_container)
        val createEventButton = findViewById<Button>(R.id.create_event_button)
        val viewEventsButton = findViewById<Button>(R.id.view_events_button)

        // Create a list of events with their details
        val events = listOf(
            Event(
                1,
                "Science Fair",
                "Come see the exciting science projects our students have been working on!",
                "Surat, Gujarat",
                "June 5, 2023",
                "12:00 AM",
                R.drawable.science_fair
            ),
            Event(
                2,
                "Math Competition",
                "Come see the exciting math projects our students have been working on!",
                "Surat, Gujarat",
                "June 5, 2023",
                "12:00 AM",
                R.drawable.math_competition
            ),
            Event(
                3,
                "Science Fair",
                "Come see the exciting science projects our students have been working on!",
                "Surat, Gujarat",
                "June 5, 2023",
                "12:00 AM",
                R.drawable.science_fair
            )
        )

        // Loop through the list of events and create a CardView for each event
        for (event in events) {
            val cardView =
                layoutInflater.inflate(R.layout.upcoming_event_card, null) as CardView
            cardView.findViewById<TextView>(R.id.event_name).text = event.name
            cardView.findViewById<TextView>(R.id.event_date).text = event.date
            cardView.findViewById<TextView>(R.id.event_time).text = event.time
            cardView.findViewById<ImageView>(R.id.event_image).setImageResource(event.image)
            cardView.findViewById<TextView>(R.id.event_description).text = event.description

            // Add the CardView to the parent layout
            upcomingEvents.addView(cardView)
        }

        //card for adding upcoming events
        val emptyCardView = layoutInflater.inflate(R.layout.empty_upcoming_event_card, null) as CardView
        // Set an OnClickListener on the empty card view
        emptyCardView.setOnClickListener {
            // Show dialog box
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Add New Event")
            dialogBuilder.setMessage("Are you sure you want to add a new event?")
            dialogBuilder.setPositiveButton("Yes") { _, _ ->
                // Remove empty card view
                upcomingEvents.removeView(emptyCardView)

                // Inflate upcoming_event_card layout and add to layout
                val newCardView = layoutInflater.inflate(R.layout.upcoming_event_card, null) as CardView
                upcomingEvents.addView(newCardView)

                // Show form for user to input new event details
                val newEventDialog = AlertDialog.Builder(this).create()
                val newEventView = layoutInflater.inflate(R.layout.new_upcoming_event_dialog, null)

                newEventDialog.setView(newEventView)

                // Set up calendar and time pickers
                val datePicker = newEventView.findViewById<ImageView>(R.id.new_upcoming_event_date_icon)
                val timePicker = newEventView.findViewById<ImageView>(R.id.new_upcoming_event_time_icon)
                val dateEditText = newEventView.findViewById<EditText>(R.id.event_date_input)
                val timeEditText = newEventView.findViewById<EditText>(R.id.event_time_input)
                val calendar = Calendar.getInstance()

                datePicker.setOnClickListener {
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

                    // Show date picker dialog
                    val datePickerDialog = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
                        // Update calendar with new date
                        calendar.set(year, monthOfYear, dayOfMonth)

                        // Update date edit text with new date
                        dateEditText.setText(SimpleDateFormat("EEE, MMM d, yyyy").format(calendar.time))
                    }, year, month, dayOfMonth)

                    // Set minimum date to today's date
                    datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

                    datePickerDialog.show()
                }

                timePicker.setOnClickListener {
                    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = calendar.get(Calendar.MINUTE)

                    // Show time picker dialog
                    val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
                        // Update calendar with new time
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)

                        // Update time edit text with new time
                        timeEditText.setText(SimpleDateFormat("h:mm a").format(calendar.time))
                    }, hourOfDay, minute, false)

                    timePickerDialog.show()
                }

                // Set up image picker
                val selectImageButton = newEventView.findViewById<Button>(R.id.select_image_button)
                selectImageButton.setOnClickListener {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, IMAGE_PICKER_REQUEST_CODE)
                }

                // Set click listener for "Save" button
                newEventView.findViewById<Button>(R.id.save_event_button).setOnClickListener {
                    // Get user input from form
                    val eventName = newEventView.findViewById<EditText>(R.id.event_name_input).text.toString()
                    val eventDate = SimpleDateFormat("EEE, MMM d, yyyy").format(calendar.time)
                    val eventTime = SimpleDateFormat("h:mm a").format(calendar.time)
                    val eventDescription = newEventView.findViewById<EditText>(R.id.event_description_input).text.toString()

                    // Set event details in card view
                    newCardView.findViewById<TextView>(R.id.event_name).text = eventName
                    newCardView.findViewById<TextView>(R.id.event_date).text = eventDate
                    newCardView.findViewById<TextView>(R.id.event_time).text = eventTime
                    newCardView.findViewById<TextView>(R.id.event_description).text = eventDescription

                    upcomingEvents.addView(emptyCardView)
                    newEventDialog.dismiss()
                }

                // Set click listener for "Cancel" button
                newEventView.findViewById<Button>(R.id.cancel_button).setOnClickListener {
                    upcomingEvents.removeView(newCardView)
                    upcomingEvents.addView(emptyCardView)
                    newEventDialog.dismiss()
                }

                newEventDialog.show()
            }

            dialogBuilder.setNegativeButton("No") { _, _ ->
                // Do nothing
            }

            dialogBuilder.show()
        }


        upcomingEvents.addView(emptyCardView)

        createEventButton.setOnClickListener {
            val intent = Intent(this, CreateEventActivity::class.java)
            startActivity(intent)
        }

        viewEventsButton.setOnClickListener {
            val intent = Intent(this, ViewEventsActivity::class.java)
            startActivity(intent)
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the selected image URI
            val imageUri = data.data

            // Update the image view with the selected image using Glide
            val imageView = findViewById<ImageView>(R.id.event_image_view)
            Glide.with(this)
                .load(imageUri)
                .into(imageView)
        }
    }

}
