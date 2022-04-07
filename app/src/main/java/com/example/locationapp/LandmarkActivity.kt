package com.example.locationapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class LandmarkActivity : AppCompatActivity() {

    private lateinit var txtTitle : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmark)

        txtTitle = findViewById<TextView>(R.id.txtTitle)

        val locationId = intent.getStringExtra("landmark_id")

        txtTitle.text = "Welcome to " + locationId


    }
}