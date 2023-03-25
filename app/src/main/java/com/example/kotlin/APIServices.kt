package com.example.kotlin

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

const val BASE_URL = "http://192.168.1.6:3000/v1/"
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
//    val left_seats: Int ADMIN DOESN'T NEED THIS
)

data class BusResponse(
    var count: Int,
    val data: List<Bus>
)

data class BusStationResponse(
    val data: List<BusStation>
)

// Structure of body for api request
data class AdminBusCreateBody (
    val bo_id: String,
    val start_point: String,
    val end_point : String,
    val type: Int,
    val start_time: String,
    val end_time : String,
    val image_url: String,
    val policy: String,
    val num_of_seats: Int,
    val price: Int
)


interface BusService {
    @GET("/bus/search")
    fun searchBusses(): Call<BusResponse>;

    // Admin create bus

    @POST("admin/bus/create")
    fun adminCreateBus(
        @Header("Authorization") token: String,
        @Body bus: AdminBusCreateBody): Call<Bus>
}

interface BusStationService {
    @GET("/bus-station/list")
    fun getBusStations(): Call<BusStationResponse>;
}

class APIServiceImpl {
    val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    fun searchBusses(): BusService {
        return api.create(BusService::class.java)
    }

    fun getAllBusStations(): BusStationService {
        return api.create(BusStationService::class.java)
    }

    // Admin create bus
    fun adminCreateBus(): BusService {
        return  api.create(BusService::class.java)
    }
}