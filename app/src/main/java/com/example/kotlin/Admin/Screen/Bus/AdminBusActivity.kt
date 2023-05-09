package com.example.kotlin.Admin.Screen.Bus

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin.*
import com.example.kotlin.Admin.Screen.Bus.AdminBusCreateActivity
import com.example.kotlin.jsonConvert.Buses
import kotlinx.coroutines.*
import retrofit2.awaitResponse

class AdminBusActivity:AppCompatActivity() {
    lateinit var busRV: RecyclerView
    lateinit var buses: MutableList<Buses>
    lateinit var addBtn: ImageButton
    lateinit var backBtn: ImageButton
    var busAdapter: AdminBusAdapter? = null
    val retrofit = APIServiceImpl()

    private val REQUEST_CODE = 1
    private var isLoading = false

    private var page = 0
    private var limit = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_bus)


        buses = mutableListOf()
        busRV = findViewById(R.id.adminBusRV)


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
        val token = "BEARER " + UserInformation.TOKEN
        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }



        // Load bus list into recycle view
        GlobalScope.launch (Dispatchers.IO + coroutineExceptionHandler) {
            Log.d("token", token!!)
            var response = retrofit.adminGetBuses().adminGetBuses(token!!, page, limit).awaitResponse() // CHANGE
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
                    busRV.adapter = busAdapter
                    busRV.layoutManager = LinearLayoutManager(this@AdminBusActivity,
                        LinearLayoutManager.VERTICAL,false)


                }

            }



            //  DELETE 1 BOOKING
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

        // RecycleView load more items
        busRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (lastVisibleItemPosition == totalItemCount - 1 && !isLoading) {
                    isLoading = true

                    // Load more items here from your data source
                    // Add the new items to the adapter's data set
                    // Notify the adapter that new items have been added
                    page ++
                    GlobalScope.launch (Dispatchers.IO + coroutineExceptionHandler) {

                        var response = retrofit.adminGetBuses().adminGetBuses(token!!, page, limit).awaitResponse() // CHANGE
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

//                                busAdapter = AdminBusAdapter(buses)
                                busAdapter!!.notifyItemRangeInserted(page * limit, (page + 1) * limit - 1)

                            }

                        }
                    }


                    isLoading = false
                }
            }
        })
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