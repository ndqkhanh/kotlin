package com.example.kotlin

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class BusStation (
    val id: String,
    val name: String,
    val location: String
)

data class BusOperator (
    val id: Int,
    val image_url: String,
    val phone: String,
    val name: String
        )

data class Bus(
    val id: Int,
    val bo_id: String,
    val start_point: BusStation,
    val end_point: BusStation,
    val type: Int,
    val start_time: String,
    val end_time: String,
    val image_url: String,
    val num_of_seats: Int,
    val price: Int,
    val bus_operators: BusOperator,
    val left_seats: Int
)

data class BusResponse(
    var count: Int,
    val data: List<Bus>
)

interface BusService {
    @GET("/bus/search")
    fun searchBusses(): Call<BusResponse>;
}

data class BusStationResponse(
    val data: List<BusStation>
)

interface BusStationService {
    @GET("/v1/bus-station/list")
    fun getBusStations(): Call<BusStationResponse>;
}

class APIServiceImpl {
    val api = Retrofit.Builder()
        .baseUrl("http://10.123.1.217:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    fun searchBusses(): BusService {
        return api.create(BusService::class.java)
    }

    fun getAllBusStations(): BusStationService {
        return api.create(BusStationService::class.java)
    }
}