package com.example.kotlin.jsonConvert

data class Buses(
    val bo_id: String,
    val bus_operators: BusOperators,
    val bus_stations_bus_stationsTobuses_end_point: BusStationsBusStationsTobusesEndPoint,
    val bus_stations_bus_stationsTobuses_start_point: BusStationsBusStationsTobusesStartPoint,
    val end_point: String,
    val end_time: String,
    val id: String,
    val image_url: String,
    val num_of_seats: Int,
    val policy: String,
    val price: Int,
    val start_point: String,
    val start_time: String,
    val type: Int
)