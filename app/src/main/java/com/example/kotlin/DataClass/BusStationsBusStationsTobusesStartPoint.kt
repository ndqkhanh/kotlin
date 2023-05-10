package com.example.kotlin.DataClass

import kotlinx.serialization.Serializable

@Serializable
data class BusStationsBusStationsTobusesStartPoint(
    val id: String,
    val location: String,
    val name: String
)