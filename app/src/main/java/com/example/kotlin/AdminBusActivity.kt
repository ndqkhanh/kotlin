package com.example.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class AdminBusActivity:AppCompatActivity() {
    lateinit var busTicketRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_bus)

        busTicketRV = findViewById(R.id.adminBusRV)


    }
}