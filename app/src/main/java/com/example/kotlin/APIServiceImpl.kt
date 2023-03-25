package com.example.kotlin

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class BusStation(
    val id: Int,
    val name: String,
    val location: String
)

data class BusOperator(
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

data class TicketData(
    val name: String,
    val phone: String,
    val seat_positions: Int
)

data class TicketResponse(
    val seat_positions: String,
    val ticket_ids: String,
    val name: String,
    val email: String,
    val bo_name: String,
    val start_point: String,
    val end_point: String,
    val start_time: String,
    val end_time: String,
    val duration: String,
    val policy: String,
    val num_of_seats: Int,
    val type: String,
    val ticket_cost: String,
    val total_cost: String,
    val status: String
)


interface BusService {
    @GET("/bus/search")
    fun searchBusses(): Call<BusResponse>
}

interface TicketService {
    @POST("/ticket/create/:busId")
    fun createTicket(
        @Path("busId") busId: String,
        @Body ticketData: TicketData
    ): Call<TicketResponse>
}


class APIServiceImpl {
    fun searchBusses(): BusResponse {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://localhost:3000/v1")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(BusService::class.java).searchBusses().execute().body()!!
    }

    fun createTicket(busId: String, ticketData: TicketData): TicketResponse {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://localhost:3000/v1")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(TicketService::class.java)
            .createTicket(busId, ticketData).execute().body()!!
    }
}