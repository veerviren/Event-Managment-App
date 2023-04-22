package com.example.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

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

        createEventButton.setOnClickListener {
            val intent = Intent(this, CreateEventActivity::class.java)
            startActivity(intent)
        }

        viewEventsButton.setOnClickListener {
            val intent = Intent(this, ViewEventsActivity::class.java)
            startActivity(intent)
        }
    }
}
