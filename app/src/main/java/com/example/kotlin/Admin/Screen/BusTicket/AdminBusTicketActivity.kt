package com.example.kotlin.Admin.Screen.BusTicket

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin.*
import kotlinx.coroutines.*
import retrofit2.awaitResponse

class AdminBusTicketActivity:AppCompatActivity() {
    lateinit var busTicketRV: RecyclerView
    lateinit var busTickets: MutableList<BusTicket>
    lateinit var backBtn: ImageButton
    var busTicketAdapter: AdminBusTicketAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_bus_ticket)
        val token = "BEARER " + UserInformation.TOKEN
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
                    val space = 50
                    val itemDecoration = SpaceItemDecoration(space)

                    busTicketAdapter = AdminBusTicketAdapter(busTickets)
                    busTicketRV = findViewById(R.id.adminBusTicketRV)
                    busTicketRV.adapter = busTicketAdapter
                    busTicketRV.layoutManager = LinearLayoutManager(this@AdminBusTicketActivity,LinearLayoutManager.VERTICAL,false)


                    busTicketRV.addItemDecoration(itemDecoration)
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


        backBtn = findViewById(R.id.adminBusTicketListBackBtn)
        backBtn.setOnClickListener {
//            Intent(this, AdminActivity::class.java).also {
//                startActivity(it)
//            }
            finish()
        }

    }
}