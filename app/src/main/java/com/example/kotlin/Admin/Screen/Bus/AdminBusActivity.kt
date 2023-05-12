package com.example.kotlin.Admin.Screen.Bus

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin.*
import com.example.kotlin.DataClass.Buses
import com.example.kotlin.User.Screen.BusSearch
import com.example.kotlin.User.Screen.ChiTietChuyenXe
import com.example.kotlin.utils.UserInformation
import kotlinx.coroutines.*
import retrofit2.awaitResponse

class AdminBusActivity:AppCompatActivity() {
    lateinit var busRV: RecyclerView
    lateinit var buses: MutableList<Buses>
    lateinit var addBtn: ImageButton
    lateinit var backBtn: ImageButton
    var busAdapter: AdminBusAdapter? = null
    val retrofit = APIServiceImpl
    var currentBusType: String = ""
    var currentBusOperator: String? = ""
    var currentBusPricing: Int = 0
    lateinit var listBusOperators: ArrayList<BusOperator>

    private val REQUEST_CODE = 1
    private var isLoading = false

    private var page = 0
    private var limit = 10

    val token = "BEARER " + UserInformation.TOKEN

    // search bus based on filter (page, limit, busOperator, type, price)
    private fun loadMoreResult(page: Int = 0, limit: Int = 10){

        val busOperatorId = if(currentBusOperator == "") null else currentBusOperator
        val pricing = currentBusPricing
        var typeOfSeatValue = if(currentBusType == "") 1 else currentBusType.toInt()
        Log.d("Search", "page: $page - $limit - $busOperatorId - $pricing - $typeOfSeatValue")


        GlobalScope.launch (Dispatchers.IO) {
            val response = retrofit.adminService().searchBuses(token!!, page, limit, AdminBusesSearchRequest(pricing, typeOfSeatValue, busOperatorId)).awaitResponse()
            Log.d("Search", response.toString())
            if(response.isSuccessful){
                var data = response.body()?.data as ArrayList<Buses>
                if(page == 0)
                    buses = data
                else
                    buses.addAll(data)
                withContext(Dispatchers.Main) {

                    if(page == 0){
                        busAdapter = AdminBusAdapter(buses)
                        busRV!!.adapter = busAdapter

                    }
                    busAdapter?.notifyItemRangeInserted(page * limit, (page -1 ) * limit)

                    //  DELETE 1 BOOKING
                    busAdapter?.onButtonClick = {bus ->
                        GlobalScope.launch (Dispatchers.IO) {
                            Log.d("Button clicked" , bus.id)
                            val result = retrofit.adminService().deleteBus(token, bus.id).awaitResponse()

                            withContext(Dispatchers.Main){
                                val pos = buses.indexOf(bus)
                                buses.removeAt(pos)
                                busAdapter?.notifyItemRemoved(pos)
                            }
                        }

                    }
                }
            }
        }
    }

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
        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }



        // Load bus list into recycle view
        GlobalScope.launch (Dispatchers.IO + coroutineExceptionHandler) {
            Log.d("token", token!!)
            var response = retrofit.adminService().getBuses(token!!, page, limit).awaitResponse() // CHANGE
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
                    val result = retrofit.adminService().deleteBus(token, bus.id).awaitResponse()

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

                        var response = retrofit.adminService().getBuses(token!!, page, limit).awaitResponse() // CHANGE
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

        // Filter the bus based on:  type
        val filterBusType = findViewById<Button>(R.id.adminBusFilterBusType)
        filterBusType.setOnClickListener {
            FragmentOperatorFilter().apply {
                var listBusType = ArrayList<ListItemFormat>()
                listBusType.add(ListItemFormat("0", "Hạng sang"))
                listBusType.add(ListItemFormat("1", "Thường"))
                listBusType.add(ListItemFormat("2", "Giường"))

                // pass listBusOperators to FragmentOperatorFilter
                arguments = Bundle().apply {
                    putSerializable("listFilter", listBusType)
                    putString("titleName", "Chọn loại xe")

                    if(currentBusType != ""){
                        putString("defaultId", currentBusType)
                    }
                }
                show(supportFragmentManager, FragmentOperatorFilter.TAG)

                // onItemClick with data
                onItemClick = onItemClick@{item ->
                    if(item.id == ""){
                        currentBusType = ""
                        filterBusType.text = "LOẠI XE"
                        filterBusType.backgroundTintList = getColorStateList(R.color.white)
                        loadMoreResult()
                        return@onItemClick
                    }
                    currentBusType = item.id
                    filterBusType.backgroundTintList = getColorStateList(R.color.teal_200)
                    loadMoreResult()
                }
            }
        }

        // Filter the bus based on: bus operator
        val filterBusOperator = findViewById<Button>(R.id.adminBusFilterBusOperator)
        filterBusOperator.setOnClickListener {
            FragmentOperatorFilter().apply {
                // convert listBusOperators to listBusOperator
                var listBusOperator = ArrayList<ListItemFormat>()
                listBusOperators.forEach {
                    listBusOperator.add(ListItemFormat(it.id, it.name))
                }

                // pass listBusOperators to FragmentOperatorFilter
                arguments = Bundle().apply {
                    putSerializable("listFilter", listBusOperator)
                    putString("titleName", "Chọn nhà xe")

                    if(currentBusOperator != ""){
                        putString("defaultId", currentBusOperator)
                    }
                }
                show(supportFragmentManager, FragmentOperatorFilter.TAG)
                // onItemClick with data
                onItemClick = onItemClick@{ item ->
                    if(item.id == ""){
                        currentBusOperator = ""
                        filterBusOperator.text = "NHÀ XE"
                        filterBusOperator.backgroundTintList = getColorStateList(R.color.white)
                        loadMoreResult()
                        return@onItemClick
                    }
                    currentBusOperator = item.id
                    filterBusOperator.backgroundTintList = getColorStateList(R.color.teal_200)
                    loadMoreResult()
                }
            }
        }

        // Filter the bus based on:  price
        val filterBusPricing = findViewById<Button>(R.id.adminBusFilterBusPricing)
        filterBusPricing.setOnClickListener {
            PricingFilter().apply {
                arguments = Bundle().apply {
                    if(currentBusPricing != 0){
                        putInt("defaultPricing", currentBusPricing)
                    }
                }
                show(supportFragmentManager, "PricingFilter")
                onApply = onApply@{ pricing ->
                    if(pricing == 0){
                        currentBusPricing = 0
                        filterBusPricing.text = "GIÁ"
                        filterBusPricing.backgroundTintList = getColorStateList(R.color.white)
                        loadMoreResult()
                        return@onApply
                    }
                    currentBusPricing = pricing
                    filterBusPricing.backgroundTintList = getColorStateList(R.color.teal_200)
                    loadMoreResult()
                }
            }
        }

        GlobalScope.launch (Dispatchers.IO) {
            val response2 = APIServiceImpl.busOperatorService().getBusOperators().awaitResponse()
            if(response2.isSuccessful){
                listBusOperators = response2.body()?.data as ArrayList<BusOperator>
                // push all bus operator to list
                listBusOperators.add(0, BusOperator("all", "", "", "All"))

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
                var response = retrofit.adminService().searchBus(token!!,id!!).awaitResponse() // CHANGE
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