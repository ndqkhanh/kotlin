package com.example.kotlin.jsonConvert

data class History(
    val bus_id: String,
    val buses: Buses,
    val create_time: String,
    val id: String,
    val name: String,
    val phone: String,
    val seat: String,
    val status: Int,
    val update_time: String,
    val user_id: String
)