package com.example.kotlin

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

data class TicketPaymentData(
    val ticket_ids: List<String>,
)

data class TicketPaymentResponse(
    val message: String
)

class TicketData(
    val phone: String,
    val num_of_seats: Int
)

data class TicketResponse(
    val seat_positions: List<Int>,
    val ticket_ids: List<String>,
    val name: String,
    val email: String,
    val phone: String,
    val bo_name: String,
    val start_point: String,
    val end_point: String,
    val start_time: String,
    val end_time: String,
    val duration: String,
    val num_of_seats: Int,
    val type: String,
    val ticket_cost: String,
    val total_cost: String,
    val status: String,
    val error: String,
)

data class BusStation(
    val id: String,
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

interface BusService {
    @GET("/bus/search")
    fun searchBusses(): Call<BusResponse>
}

data class BusStationResponse(
    val data: List<BusStation>
)

interface BusStationService {
    @GET("/v1/bus-station/list")
    fun getBusStations(): Call<BusStationResponse>
}

interface TicketService {
    @POST("/v1/ticket/create/{busId}")
    fun createTicketByNumOfSeats(
        @Path("busId") busId: String,
        @Body ticketData: TicketData
    ): Call<TicketResponse>
}

interface PaymentService {
    @POST("/v1/ticket/payment")
    fun createPaymentByTicketIds(
        @Body ticketPaymentData: TicketPaymentData
    ): Call<TicketPaymentResponse>
}

class APIServiceImpl {
    private val BASE_URL = "http://192.168.1.12:3000/"
    private val api: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun searchBusses(): BusService {
        return api.create(BusService::class.java)
    }

    fun getAllBusStations(): BusStationService {
        return api.create(BusStationService::class.java)
    }

    fun createTicket(token: String): TicketService {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(TicketService::class.java)
    }

    fun createPayment(token: String): PaymentService {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(PaymentService::class.java)
    }
}