package com.example.kotlin

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class BusStation (
    val id: Int,
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

class APIServiceImpl {
    fun searchBusses(): BusResponse {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://localhost:3000/v1")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(BusService::class.java)
    }
}