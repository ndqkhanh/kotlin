package com.example.kotlin.jsonConvert

import kotlinx.serialization.Serializable

@Serializable
data class BusStationsBusStationsTobusesEndPoint(
    val id: String,
    val location: String,
    val name: String
)