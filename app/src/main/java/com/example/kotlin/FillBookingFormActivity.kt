package com.example.kotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class FillBookingFormActivity : AppCompatActivity() {
    private lateinit var btnSubmit: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fill_booking_form)

        btnSubmit = findViewById(R.id.btnSubmit)
        btnSubmit.setOnClickListener {
            Intent(this, BookingDetailActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}