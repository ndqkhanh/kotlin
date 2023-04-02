package com.example.kotlin

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import retrofit2.await
import retrofit2.awaitResponse

class AdminBusTicketActivity:AppCompatActivity() {
    lateinit var busTicketRV: RecyclerView
    lateinit var busTickets: MutableList<BusTicket>
    var busTicketAdapter: AdminBusTicketAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_bus_ticket)

        val token = "BEARER eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI3ZmU0YTNlZS0zMjRiLTQ0NWQtODYzYy0wN2ZjNzAyYmQ4NDQiLCJpYXQiOjE2ODA0MTY1ODUsImV4cCI6MTY4MDQxODM4NSwidHlwZSI6ImFjY2VzcyJ9.yTuZ-GAF3yQ_YatqqsrnzmI9ywaw7QXS2VPUymUnSPc"
        val retrofit = APIServiceImpl()
        busTickets = mutableListOf()

        // LOC 21 -> 42: fetch data
        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }

        GlobalScope.launch (Dispatchers.IO + coroutineExceptionHandler) {

            var response = retrofit.adminBookingList().getBookingList(token).awaitResponse()
            Log.d("Response", "vui 1" + response.message())
            // debug response
            Log.d("Response", response.toString())
            if(response.isSuccessful){
                Log.d("Response", "vui 2")
                val data = response.body()!!
                Log.d("Response", data.toString())
                for (it in data.data) busTickets.add(it)

                Log.d("busTickets vui 1: ", busTickets.size.toString())

                withContext(Dispatchers.Main){
                    busTicketAdapter = AdminBusTicketAdapter(busTickets)
                    busTicketRV = findViewById(R.id.adminBusTicketRV)
                    busTicketRV.adapter = busTicketAdapter
                    busTicketRV.layoutManager = LinearLayoutManager(this@AdminBusTicketActivity,LinearLayoutManager.VERTICAL,false)

                }

            }

            // TODO DELETE 1 BOOKING
            busTicketAdapter?.onButtonClick = {busTicket ->
                GlobalScope.launch (Dispatchers.IO) {
                    Log.d("Button clicked" , busTicket.bus_id)
                    val result = retrofit.adminDeleteBooking().deleteBooking(token, busTicket.id).awaitResponse()

                    withContext(Dispatchers.Main){
                        val pos = busTickets.indexOf(busTicket)
                        busTicketAdapter?.notifyItemRemoved(pos)
                    }
                }


            }

            }


    }
}