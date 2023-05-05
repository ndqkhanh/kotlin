package com.example.kotlin

import com.example.kotlin.jsonConvert.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import retrofit2.http.*


data class BlogResponse(
    val id: String,
    val thumbnail: String,
    val title: String,
    val content: String,
    val create_time: String,
    val update_time: String,
)

data class BlogListResponse(
    val count: Int,
    val data: List<BlogResponse>
)

data class BlogData(
    val thumbnail: String,
    val title: String,
    val content: String,
)

data class BlogDeleteResponse(
    val success: Boolean,
)

data class TicketPaymentData(
    val ticket_ids: List<String>,
)

data class TicketPaymentResponse(
    val message: String
)

data class TicketData(
    val name: String,
    val pick_up_point: String,
    val drop_down_point: String,
    val phone: String,
    val num_of_seats: Int,
    val note: String,
)

data class TicketCreateResponse(
    val seat_positions: List<Int>,
    val ticket_ids: List<String>,
)

data class TicketResponse(
    val status: Boolean,
    val data: TicketCreateResponse,
)


data class BusStation(
    val id: String,
    val name: String,
    val location: String
)

data class PointStation(
    val id: String,
    val name: String,
    val location: String
)

data class BusOperator (
    val id: String,
    val image_url: String,
    val phone: String,
    val name: String
)
data class BusOperatorBody(
    val image_url: String,
    val phone: String,
    val name: String
)

data class Bus(
    val id: String,
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
    val pricing_format: String,
    val duration: String,
    val left_seats: Int,
    var start_time_hour: String,
    var end_time_hour: String,
    val rating: Float
)

data class BusResponse(
    var count: Int,
    val data: List<Bus>
)

data class BusSearchRequest(
    var startPoint: String,
    var endPoint: String,
    var page: Int,
    var limit: Int,
    var startTime: String,
    var price: Int?,
    var type: Int?,
    var boId: String?
    )


data class BusStationResponse(
    val data: List<BusStation>
)

data class PointResponse(
    val data: List<PointStation>
)

data class PointsByBsId(
    val point_id: String,
    val bs_id: String,
    val points: PointStation,
    val bus_stations: BusStation
)

data class PointsByBsIdResponse(
    val data: List<PointsByBsId>
)

data class BusOperatorResponse(
    val data: List<BusOperator>
)

// Structure of body for api request
data class AdminBusCreateBody(
    val bo_id: String,
    val start_point: String,
    val end_point: String,
    val type: Int,
    val start_time: String,
    val end_time: String,
    val image_url: String,
    val policy: String,
    val num_of_seats: Int,
    val price: Int
)
data class AdminBusCreateRespond(
    val id: String,
    val bo_id: String,
    val start_point: String,
    val end_point: String,
    val type: Int,
    val start_time: String,
    val end_time: String,
    val image_url: String,
    val policy: String,
    val num_of_seats: Int,
    val price: Int
)

data class BusTicket(
    val id: String,
    val bus_id: String,
    val name: String,
    val start_point: String,
    val end_point: String,
    val start_time: String,
    val end_time: String,
    val seat: String,
    val status: String,
    val phone: String
)


data class BusTicketResponse(
    val data: List<BusTicket>
)

data class DeleteBusTicketResponse(
    val success: Boolean
)
data class DeleteBusResponse(
    val success: Boolean
)
data class DeleteBusOperatorResponse(
    val success: Boolean
)
data class AdminBusesResponse (
    val data: List<Buses>
)
interface UserService {
    @POST("auth/signup")
    fun signUp(@Body signUpData: AccountSignUp): Call<UserSignUpRespone>

    @POST("auth/signin")
    fun signIn(@Body signInData: UserLogin): Call<UserLogInRespone>

    @GET("user/history")
    fun ticketHistory(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("type") type: String?
    ): Call<HistoryList>

    @GET("user/information")
    fun getUserByAccountName(@Query("username") username: String): Call<User>
}

interface BusService {
    @GET("/bus/search")
    fun searchBusses(): Call<BusResponse>

    // Admin
    @GET("admin/bus/list/${page}/${limit}")
    fun adminGetBuses(
        @Header("Authorization") token: String
    ) : Call<AdminBusesResponse>

    @GET("admin/bus/{id}")
    fun  adminSearchBus(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ) : Call<Buses>

    @POST("admin/bus/create")
    fun adminCreateBus(
        @Header("Authorization") token: String,
        @Body bus: AdminBusCreateBody
    ): Call<AdminBusCreateRespond>

    @POST("admin/bus/delete/{bid}")
    fun deleteBus(
        @Header("Authorization") token: String,
        @Path("bid") bid: String
    ): Call<DeleteBusResponse>

    @POST("/v1/bus/search")
    fun search(@Body request: BusSearchRequest): Call<BusResponse>

    @GET("/v1/bus/{busId}")
    fun getBusById(@Path("busId") busId: String): Call<Bus>
}

interface BusStationService {
    @GET("bus-station/list")
    fun getBusStations(): Call<BusStationResponse>
}

interface PointService {
    @GET("point/list")
    fun getPoints(): Call<PointResponse>

    @GET("point/list-point/{bsId}")
    fun getPointsByBsId(@Path("bsId") bsId: String): Call<PointsByBsIdResponse>
}

interface TicketService {
    @POST("ticket/create/{busId}")
    fun createTicketByNumOfSeats(
        @Path("busId") busId: String,
        @Body ticketData: TicketData
    ): Call<TicketResponse>

    @GET("admin/booking/list")
    fun getBookingList(@Header("Authorization") token: String): Call<BusTicketResponse>

    @DELETE("admin/booking/{bid}")
    fun deleteBooking(
        @Header("Authorization") token: String,
        @Path("bid") bid: String
    ): Call<DeleteBusTicketResponse>
}

interface PaymentService {
    @POST("ticket/payment")
    fun createPaymentByTicketIds(
        @Body ticketPaymentData: TicketPaymentData
    ): Call<TicketPaymentResponse>
}

interface BlogService {
    @GET("blog/list/{page}/{limit}")
    fun getBlogs(@Path("page") page: Int, @Path("limit") limit: Int): Call<BlogListResponse>

    @GET("blog/{blogId}")
    fun getBlogById(@Path("blogId") blogId: String): Call<BlogResponse>

    @POST("blog/create")
    fun createBlog(@Body blogData: BlogData): Call<BlogResponse>

    @POST("blog/delete/{blogId}")
    fun deleteBlogById(@Path("blogId") blogId: String): Call<BlogDeleteResponse>
}

const val page = 0
const val limit = 50

interface BusOperatorService {
    @GET("bus-operator/list/${page}/${limit}")
    fun getBusOperators(): Call<BusOperatorResponse>

    @DELETE("bus-operator/{bid}")
    fun deleteBusOperator(
        @Header("Authorization") token: String,
        @Path("bid") bid: String
    ): Call<DeleteBusOperatorResponse>

    @POST("bus-operator/create")
    fun createBusOperator(
        @Header("Authorization") token: String,
        @Body busOperator: BusOperatorBody): Call<BusOperator>

    @GET("bus-operator/{boId}")
    fun getBusOperator(
        @Header("Authorization") token: String,
        @Path("boId") bid: String): Call<BusOperator>
}


class APIServiceImpl {
    private val BASE_URL = "http://192.168.1.9:3000/v1/"

    private val api: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun busOperatorService(): BusOperatorService {
        return api.create(BusOperatorService::class.java)
    }

    fun userService(): UserService {
        return api.create(UserService::class.java)
    }

    fun searchBusses(): BusService {
        return api.create(BusService::class.java)
    }

    fun getAllBusStations(): BusStationService {
        return api.create(BusStationService::class.java)
    }

    fun point(): PointService {
        return api.create(PointService::class.java)
    }

    fun bus(): BusService {
        return api.create(BusService::class.java)
    }
    fun getAllBusOperators(): BusOperatorService {
        return api.create(BusOperatorService::class.java)
    }
    // Admin create bus
    fun adminDeleteBuses(): BusService {
        return api.create(BusService::class.java)
    }

    fun adminGetBuses(): BusService {
        return api.create(BusService::class.java)
    }

    fun adminCreateBus(): BusService {
        return api.create(BusService::class.java)
    }

    fun adminBookingList(): TicketService {
        return api.create(TicketService::class.java)
    }

    fun adminDeleteBooking(): TicketService {
        return api.create(TicketService::class.java)
    }

    fun adminCreateBusOperator(): BusOperatorService {
        return api.create(BusOperatorService::class.java)
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

    fun getBlog(): BlogService {
        return api.create(BlogService::class.java)
    }

    fun manipulateBlog(token: String): BlogService {
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

        return retrofit.create(BlogService::class.java)
    }
}