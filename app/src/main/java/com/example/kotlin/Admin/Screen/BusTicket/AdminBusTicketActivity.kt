package com.example.kotlin.Admin.Screen.BusTicket

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin.*
import com.example.kotlin.Admin.Screen.Bus.AdminBusAdapter
import com.example.kotlin.DataClass.Buses
import com.example.kotlin.utils.UserInformation
import kotlinx.coroutines.*
import retrofit2.awaitResponse

class AdminBusTicketActivity:AppCompatActivity() {
    lateinit var busTicketRV: RecyclerView
    lateinit var busTickets: MutableList<BusTicket>
    lateinit var backBtn: ImageButton
    var busTicketAdapter: AdminBusTicketAdapter? = null
    private lateinit var autoBooking: AutoCompleteTextView
    private var isLoading = false // use for load more items
    private var page = 0 // pagination
    private var limit = 10 // pagination
    var currentStatus: String = ""
    val retrofit = APIServiceImpl
    val token = "BEARER " + UserInformation.TOKEN
    private fun loadMoreResult(page: Int = 0, limit: Int = 10){

        val name = if(autoBooking.text.toString() == "") null else autoBooking.text.toString()
        var status = if(currentStatus == "") null else currentStatus.toInt()
        Log.d("Search", "page: $page - $limit - $name - $status")


        GlobalScope.launch (Dispatchers.IO) {
            val response = retrofit.adminService().searchBookings(token!!, page, limit, AdminBookingsSearchRequest(name, status)).awaitResponse()
            Log.d("Search", response.toString())
            if(response.isSuccessful){
                var data = response.body()?.data as ArrayList<BusTicket>
                if(page == 0)
                    busTickets = data
                else
                    for (it in data)  busTickets.add(it)
                withContext(Dispatchers.Main) {

                    if(page == 0){
                        busTicketAdapter = AdminBusTicketAdapter(busTickets)
                        busTicketRV!!.adapter = busTicketAdapter

                    }
                    busTicketAdapter?.notifyItemRangeInserted(page * limit, (page -1 ) * limit)


                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_bus_ticket)


        busTickets = mutableListOf()
        busTicketRV = findViewById(R.id.adminBusTicketRV)



        // back button
        backBtn = findViewById(R.id.adminBusTicketListBackBtn)
        backBtn.setOnClickListener {
            finish()
        }

        // Autocomplete booking
        autoBooking = findViewById(R.id.autoVe)
        autoBooking.text = null

    }

    override fun onResume() {
        super.onResume()
        // LOC 21 -> 42: fetch data
        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }

        // Load items into recycleview
        GlobalScope.launch (Dispatchers.IO + coroutineExceptionHandler) {

            var response = retrofit.adminService().getBookingList(token, page, limit).awaitResponse()
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

                    // Integrate autocomplete booking
                    val listName = busTickets.map { it.name }
                    val adapter = ArrayAdapter(
                        this@AdminBusTicketActivity,
                        android.R.layout.simple_list_item_1,
                        listName
                    )
                    autoBooking.setAdapter(adapter)
                    autoBooking.addTextChangedListener(object: TextWatcher{
                        override fun beforeTextChanged(
                            p0: CharSequence?,
                            p1: Int,
                            p2: Int,
                            p3: Int
                        ) {
                        }
                        override fun afterTextChanged(p0: Editable?) {

                        }
                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            val newBusTickets : MutableList<BusTicket>
                            newBusTickets = busTickets.filter { it -> it.name.contains(p0.toString()) } as MutableList<BusTicket>
                            Log.d("newBusTickets ", newBusTickets.toString())
                            busTicketAdapter = AdminBusTicketAdapter(newBusTickets)
                            busTicketRV.adapter = busTicketAdapter
                        }



                    })
                }

            }

            // DELETE 1 BOOKING
            busTicketAdapter?.onButtonClick = {busTicket ->
                GlobalScope.launch (Dispatchers.IO) {
                    Log.d("Button clicked" , busTicket.bus_id)
                    val result = retrofit.adminService().deleteBooking(token, busTicket.id).awaitResponse()

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
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                // Scroll to bottom
                if (lastVisibleItemPosition == totalItemCount - 1 && !isLoading) {
                    isLoading = true

                    // Load more items here from your data source
                    // Add the new items to the adapter's data set
                    // Notify the adapter that new items have been added
                    page ++
                    loadMoreResult(page, limit)
//                    GlobalScope.launch (Dispatchers.IO + coroutineExceptionHandler) {
//
//                        var response = retrofit.adminService().getBookingList(token, page, limit).awaitResponse() // CHANGE
//                        Log.d("Response", "vui 1" + response.message())
//                        // debug response
//                        Log.d("Response", response.toString())
//                        if(response.isSuccessful){
//                            Log.d("Response", "vui 2")
//                            val data = response.body()!!
//                            Log.d("Response", data.toString())
//                            for (it in data.data)  busTickets.add(it)
//
//                            Log.d("busTickets vui 1: ", busTickets.size.toString())
//
//                            withContext(Dispatchers.Main){
//
////                                busAdapter = AdminBusAdapter(buses)
//                                busTicketAdapter!!.notifyItemRangeInserted(page * limit, (page + 1) * limit - 1)
//
//                            }
//
//                        }
//                    }


                    isLoading = false
                }

                // Scroll to top
                if (firstVisibleItemPosition == 0  && !isLoading && page > 0) {
                    isLoading = true

                    // Load more items here from your data source
                    // Add the new items to the adapter's data set
                    // Notify the adapter that new items have been added
                    page --
                    loadMoreResult(page, limit)
//                    GlobalScope.launch (Dispatchers.IO + coroutineExceptionHandler) {
//
//                        var response = retrofit.adminService().getBookingList(token, page, limit).awaitResponse() // CHANGE
//                        Log.d("Response", "vui 1" + response.message())
//                        // debug response
//                        Log.d("Response", response.toString())
//                        if(response.isSuccessful){
//                            Log.d("Response", "vui 2")
//                            val data = response.body()!!
//                            Log.d("Response", data.toString())
//                            for (it in data.data)  busTickets.add(it)
//
//                            Log.d("busTickets vui 1: ", busTickets.size.toString())
//
//                            withContext(Dispatchers.Main){
//
////                                busAdapter = AdminBusAdapter(buses)
//                                busTicketAdapter!!.notifyItemRangeInserted(page * limit, (page + 1) * limit - 1)
//
//                            }
//
//                        }
//                    }


                    isLoading = false
                }
            }
        })


        // Filter the booking based on:  status
        val filterBookingStatus = findViewById<Button>(R.id.adminBusTicketFilterBusType)
        filterBookingStatus.setOnClickListener {
            FragmentOperatorFilter().apply {
                var listBookingStatus = ArrayList<ListItemFormat>()
                listBookingStatus.add(ListItemFormat("0", "Chưa thanh toán"))
                listBookingStatus.add(ListItemFormat("1", "Chờ thanh toán"))
                listBookingStatus.add(ListItemFormat("2", "Đã thanh toán"))

                // pass listBusOperators to FragmentOperatorFilter
                arguments = Bundle().apply {
                    putSerializable("listFilter", listBookingStatus)
                    putString("titleName", "Chọn trạng thái")

                    if(currentStatus != ""){
                        putString("defaultId", currentStatus)
                    }
                }
                show(supportFragmentManager, FragmentOperatorFilter.TAG)

                // onItemClick with data
                onItemClick = onItemClick@{item ->
                    if(item.id == ""){
                        currentStatus = ""
                        filterBookingStatus.text = "LOẠI XE"
                        filterBookingStatus.backgroundTintList = getColorStateList(R.color.white)
                        loadMoreResult()
                        return@onItemClick
                    }
                    currentStatus = item.id
                    filterBookingStatus.backgroundTintList = getColorStateList(R.color.teal_200)
                    loadMoreResult()
                }
            }
        }

    }
}