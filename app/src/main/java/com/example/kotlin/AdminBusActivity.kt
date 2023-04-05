package com.example.kotlin

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin.jsonConvert.Buses
import kotlinx.coroutines.*
import retrofit2.awaitResponse

class AdminBusActivity:AppCompatActivity() {
    lateinit var busRV: RecyclerView
    lateinit var buses: MutableList<Buses>
    var busAdapter: AdminBusAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_bus)

        val token = "BEARER eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI3ZmU0YTNlZS0zMjRiLTQ0NWQtODYzYy0wN2ZjNzAyYmQ4NDQiLCJpYXQiOjE2ODA2Njc4OTYsImV4cCI6MTY4MDY2OTY5NiwidHlwZSI6ImFjY2VzcyJ9.6fLzsClDbd6ry86P7DcsjeCGrFESVfWMVm5RX0rLOmw"
        val retrofit = APIServiceImpl()

        buses = mutableListOf()

        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }


        GlobalScope.launch (Dispatchers.IO + coroutineExceptionHandler) {

            var response = retrofit.adminGetBuses().adminGetBuses(token).awaitResponse() // CHANGE
            Log.d("Response", "vui 1" + response.message())
            // debug response
            Log.d("Response", response.toString())
            if(response.isSuccessful){
                Log.d("Response", "vui 2")
                val data = response.body()!!
                Log.d("Response", data.toString())
                for (it in data.data) buses.add(it)

                Log.d("busTickets vui 1: ", buses.size.toString())

                withContext(Dispatchers.Main){
                    busAdapter = AdminBusAdapter(buses)
                    busRV = findViewById(R.id.adminBusRV)
                    busRV.adapter = busAdapter
                    busRV.layoutManager = LinearLayoutManager(this@AdminBusActivity,
                        LinearLayoutManager.VERTICAL,false)

                }

            }

            // TODO DELETE 1 BOOKING
            busAdapter?.onButtonClick = {bus ->
                GlobalScope.launch (Dispatchers.IO) {
                    Log.d("Button clicked" , bus.id)
                    val result = retrofit.adminDeleteBuses().deleteBus(token, bus.id).awaitResponse()

                    withContext(Dispatchers.Main){
                        val pos = buses.indexOf(bus)
                        busAdapter?.notifyItemRemoved(pos)
                    }
                }


            }

        }





    }
}