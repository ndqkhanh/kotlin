package com.example.kotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin.jsonConvert.Buses
import kotlinx.coroutines.*
import retrofit2.awaitResponse

class AdminBusActivity:AppCompatActivity() {
    lateinit var busRV: RecyclerView
    lateinit var buses: MutableList<Buses>
    lateinit var addBtn: Button
    lateinit var backBtn: ImageButton
    var busAdapter: AdminBusAdapter? = null
    val retrofit = APIServiceImpl()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_bus)

        val token = "BEARER " + this.getSharedPreferences("vexere", MODE_PRIVATE).getString("token", "")


        buses = mutableListOf()

        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }


        GlobalScope.launch (Dispatchers.IO + coroutineExceptionHandler) {
            Log.d("token", token!!)
            var response = retrofit.adminGetBuses().adminGetBuses(token!!).awaitResponse() // CHANGE
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



        // Add Button
        addBtn = findViewById(R.id.adminBusAddBtn)
        addBtn.setOnClickListener {
            Intent(this, AdminBusCreateActivity::class.java).also {
                startActivity(it)
            }
        }

        backBtn = findViewById(R.id.adminBusListBackBtn)
        backBtn.setOnClickListener{
            Intent(this,AdminActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }
}