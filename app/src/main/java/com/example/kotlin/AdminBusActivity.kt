package com.example.kotlin

import android.app.Activity
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

    private val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_bus)


        buses = mutableListOf()



        // Add Button
        addBtn = findViewById(R.id.adminBusAddBtn)
        addBtn.setOnClickListener {
            val intent = Intent(this, AdminBusCreateActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        backBtn = findViewById(R.id.adminBusListBackBtn)
        backBtn.setOnClickListener{
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
//        val token = "BEARER " + this.getSharedPreferences("vexere", MODE_PRIVATE).getString("token", "")
        val token = "BEARER " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI3ZmU0YTNlZS0zMjRiLTQ0NWQtODYzYy0wN2ZjNzAyYmQ4NDQiLCJpYXQiOjE2ODIyNDUxNzgsImV4cCI6MTY4MjI0Njk3OCwidHlwZSI6ImFjY2VzcyJ9.zxTfVD-Wczdxxv4gYK4OGJVRiIOUaJFzkA0EwX8Fe3w"
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
                    val space = 5
                    val itemDecoration = SpaceItemDecoration(space)

                    busAdapter = AdminBusAdapter(buses)
                    busRV = findViewById(R.id.adminBusRV)
                    busRV.adapter = busAdapter
                    busRV.layoutManager = LinearLayoutManager(this@AdminBusActivity,
                        LinearLayoutManager.VERTICAL,false)

                    busRV.addItemDecoration(itemDecoration)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val id = data?.getStringExtra("id")

            val token = "BEARER " + this.getSharedPreferences("vexere", MODE_PRIVATE).getString("token", "")
            // Do something with the user input
            val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
                throwable.printStackTrace()
            }


            GlobalScope.launch (Dispatchers.IO + coroutineExceptionHandler) {
                Log.d("token", token!!)
                var response = retrofit.searchBusses().adminSearchBus(token!!,id!!).awaitResponse() // CHANGE
                Log.d("Response", "vui 1" + response.message())
                // debug response
                Log.d("Response", response.toString())
                if(response.isSuccessful){
                    Log.d("Response", "vui 2")
                    val data = response.body()!!
                    Log.d("Response", data.toString())
                    buses.add(0,data)

                    Log.d("busTickets vui 1: ", buses.size.toString())

                    withContext(Dispatchers.Main){
                        busAdapter?.notifyItemInserted(0)

                    }

                }

            }
        }
    }
}