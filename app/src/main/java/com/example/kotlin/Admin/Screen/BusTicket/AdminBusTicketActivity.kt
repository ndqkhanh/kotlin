package com.example.kotlin.Admin.Screen.BusTicket

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin.*
import com.example.kotlin.utils.UserInformation
import kotlinx.coroutines.*
import retrofit2.awaitResponse

class AdminBusTicketActivity:AppCompatActivity() {
    lateinit var busTicketRV: RecyclerView
    lateinit var busTickets: MutableList<BusTicket>
    lateinit var backBtn: ImageButton
    var busTicketAdapter: AdminBusTicketAdapter? = null
    private var isLoading = false // use for load more items
    private var page = 0 // pagination
    private var limit = 10 // pagination
    val retrofit = APIServiceImpl()
    val token = "BEARER " + UserInformation.TOKEN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_bus_ticket)


        busTickets = mutableListOf()
        busTicketRV = findViewById(R.id.adminBusTicketRV)



        // back button
        backBtn = findViewById(R.id.adminBusTicketListBackBtn)
        backBtn.setOnClickListener {
//            Intent(this, AdminActivity::class.java).also {
//                startActivity(it)
//            }
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        // LOC 21 -> 42: fetch data
        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }

        // Load items into recycleview
        GlobalScope.launch (Dispatchers.IO + coroutineExceptionHandler) {

            var response = retrofit.adminBookingList().getBookingList(token, page, limit).awaitResponse()
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
                    val space = 50
                    val itemDecoration = SpaceItemDecoration(space)

                    busTicketAdapter = AdminBusTicketAdapter(busTickets)

                    busTicketRV.adapter = busTicketAdapter
                    busTicketRV.layoutManager = LinearLayoutManager(this@AdminBusTicketActivity,LinearLayoutManager.VERTICAL,false)


                    busTicketRV.addItemDecoration(itemDecoration)
                }

            }

            // DELETE 1 BOOKING
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

        // RecycleView load more items
        busTicketRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

                        var response = retrofit.adminBookingList().getBookingList(token, page, limit).awaitResponse() // CHANGE
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

//                                busAdapter = AdminBusAdapter(buses)
                                busTicketAdapter!!.notifyItemRangeInserted(page * limit, (page + 1) * limit - 1)

                            }

                        }
                    }


                    isLoading = false
                }
            }
        })
    }
}