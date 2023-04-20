package com.example.kotlin

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.content.SharedPreferences
import android.provider.MediaStore.Audio.Radio
import android.view.View.VISIBLE
import androidx.core.view.isVisible
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.facebook.login.widget.LoginButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class Home : AppCompatActivity() {
    lateinit var listBusStations: ArrayList<BusStation>
    lateinit var listBusOperators: ArrayList<BusOperator>
    lateinit var spinnerDeparture: Spinner
    lateinit var spinnerDestination: Spinner
    lateinit var spinnerBusOperator: Spinner
    lateinit var datePicker: EditText
    lateinit var searchResult: ListView
    lateinit var loadMoreButton: Button
    lateinit var typeOfSeatRadioGroup: RadioGroup
    lateinit var pricingSeekBar: SeekBar
    lateinit var adminBtn: ImageButton
    lateinit var adminTextBtn: TextView
    val retrofit = APIServiceImpl()
    private lateinit var localEditor: SharedPreferences.Editor
    var listBuses = ArrayList<Bus>()
    var currentPage = 0

    private fun loadMoreResult(page: Int = 0, limit: Int = 10){
        Log.d("Search", "page: $page - $limit")
        val startTime = datePicker.text.toString()

        val departure = spinnerDeparture.selectedItem.toString()
        val destination = spinnerDestination.selectedItem.toString()

        val departureId = listBusStations.filter { it.name == departure }.map { it.id }.first()
        val destinationId = listBusStations.filter { it.name == destination }.map { it.id }.first()
        val busOperatorSelectedOption = spinnerBusOperator.selectedItem.toString()
        val busOperatorId = if(busOperatorSelectedOption == "All") null else listBusOperators.filter { it.name == busOperatorSelectedOption }.map { it.id }.first()
        val pricing = pricingSeekBar.progress
        val typeOfSeat = typeOfSeatRadioGroup.checkedRadioButtonId

        // convert typeOfSeat to value {"id": 0}
        var typeOfSeatValue = 0
        if(typeOfSeat == R.id.limousine){
            typeOfSeatValue = 0
        } else if(typeOfSeat == R.id.normalSeat){
            typeOfSeatValue = 1
        } else if(typeOfSeat == R.id.sleeperBus){
            typeOfSeatValue = 2
        }

        if(departure == "" || destination == "" || startTime == ""){
            Toast.makeText(this, "Please fill enough information", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("Search", "$departureId - $destinationId - $startTime")
            val inputDateString = "03/25/2023"
            val inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val localDate = LocalDate.parse(inputDateString, inputFormatter)
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val localDateTime = localDate.atStartOfDay()
            val zonedDateTime = localDateTime.atZone(ZoneId.of("America/New_York"))
            val outputDateString = outputFormatter.format(zonedDateTime)

            GlobalScope.launch (Dispatchers.IO) {
                val response = APIServiceImpl().searchBusses().search(BusSearchRequest(departureId, destinationId, page, limit, outputDateString, pricing, typeOfSeatValue, busOperatorId)).awaitResponse()
                Log.d("Search", response.toString())
                if(response.isSuccessful){
                    Log.d("Search", response.body().toString())
                    var data = response.body()?.data as List<Bus>
                    listBuses.addAll(data)

                    withContext(Dispatchers.Main) {
                        var adapter = CustomTicketItem(this@Home, listBuses, supportFragmentManager, lifecycle)

                        adapter.showBottomSheet = {
                            val intent = Intent(this@Home, BusDetailActivity::class.java)
                            // add busId
                            intent.putExtra("busId", it.id)
                            startActivity(intent)
                        }

                        searchResult!!.adapter = adapter

                        Utility.setListViewHeightBasedOnChildren(searchResult)
                    }
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        /*Admin Nav Button*/
        val adminButtonLayout = findViewById<LinearLayout>(R.id.admin_button_layout)
        adminBtn = findViewById(R.id.admin_button)
        if (FBInfor.ROLE == 0 || FBInfor.ROLE == 1) {
            adminButtonLayout.visibility = View.VISIBLE
        }else{
            adminButtonLayout.visibility = View.GONE
        }
        adminBtn.setOnClickListener {
            Intent(this, AdminActivity::class.java).also {
                startActivity(it)
            }
        }



        val busses = ArrayList<Bus>()
        searchResult = findViewById<ListView>(R.id.searchResult)
        val localStore = getSharedPreferences("vexere", Context.MODE_PRIVATE)
        localEditor = localStore.edit()

        var logOutListener = View.OnClickListener {
            Log.d("Response", "Đăng xuất")

            localEditor.apply {
                putString("token", null)
                commit()
            }// remove token

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
        findViewById<ImageButton>(R.id.home_logout).setOnClickListener(logOutListener)
        findViewById<TextView>(R.id.log_out_text_view).setOnClickListener(logOutListener)
        findViewById<ImageButton>(R.id.person_infor).setOnClickListener{
            val intent = Intent(this, PersonalInformation::class.java)
            startActivity(intent)
        }

        val adapter = CustomTicketItem(this, busses, supportFragmentManager, lifecycle)

        searchResult!!.adapter = adapter

//        var blogPage = 1
//        var blogLimit = 20
//        var blogList = findViewById<RecyclerView>(R.id.blogList)
//        try {
//            GlobalScope.launch(Dispatchers.IO) {
//                val response =
//                    retrofit.getBlog().getBlogs(blogPage, blogLimit).awaitResponse()
//                // debug response
//                Log.d("Response", response.toString())
//                if (response.isSuccessful) {
//                    val body = response.body()
//                    launch(Dispatchers.Main) {
//                        if (body != null) {
//                            val blogAdapter = CustomBlogItem(body.data)
//
//                            blogList!!.adapter = blogAdapter
//                            blogList.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL)
//
//                            blogAdapter.onItemClick = {
//                                val intent = Intent(this@Home, BlogDetailActivity::class.java)
//                                intent.putExtra("activity", "home")
//                                intent.putExtra("blogId", it.id)
//                                startActivity(intent)
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            Log.d("Error", e.toString())
//        }

        Utility.setListViewHeightBasedOnChildren(searchResult)

        datePicker = findViewById<EditText>(R.id.datePicker)

        datePicker.setOnClickListener {
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                Log.d("Date", "$dayOfMonth/$month/$year")
                // set date to date picker
                datePicker.setText("$dayOfMonth/$month/$year")
            }, 2020, 1, 1).show()
        }

        val retrofit = APIServiceImpl()

        spinnerDeparture = findViewById<Spinner>(R.id.spinnerDeparture)
        spinnerDestination = findViewById<Spinner>(R.id.spinnerDestination)
        spinnerBusOperator = findViewById<Spinner>(R.id.busOperatorFilter)
        typeOfSeatRadioGroup = findViewById<RadioGroup>(R.id.typeOfSeat)
        pricingSeekBar = findViewById<SeekBar>(R.id.pricingSeekBar)
        GlobalScope.launch (Dispatchers.IO) {
            val response = retrofit.getAllBusStations().getBusStations().awaitResponse()
            if(response.isSuccessful){
                listBusStations = response.body()?.data as ArrayList<BusStation>

                withContext(Dispatchers.Main) {
                    spinnerDeparture!!.adapter = ArrayAdapter(this@Home, android.R.layout.simple_list_item_single_choice, listBusStations.map { it.name })
                    spinnerDestination!!.adapter = ArrayAdapter(this@Home, android.R.layout.simple_list_item_single_choice, listBusStations.map { it.name })
                }
            }

            val response2 = retrofit.busOperatorService().getBusOperators().awaitResponse()
            if(response2.isSuccessful){
                listBusOperators = response2.body()?.data as ArrayList<BusOperator>
                // push all bus operator to list
                listBusOperators.add(0, BusOperator("all", "", "", "All"))
                Log.d("test", listBusOperators.toString())
                withContext(Dispatchers.Main) {
                    spinnerBusOperator!!.adapter = ArrayAdapter(this@Home, android.R.layout.simple_list_item_single_choice, listBusOperators.map { it.name })
                }
            }
        }

        val busOperatorFilter = findViewById<Spinner>(R.id.busOperatorFilter)

        val searchButton = findViewById<Button>(R.id.searchButton)

        searchButton.setOnClickListener {
            listBuses.clear()
            loadMoreResult()

            loadMoreButton.visibility = View.VISIBLE
        }
        
        loadMoreButton = findViewById<Button>(R.id.loadMore)
        if(currentPage == 0){
            loadMoreButton.visibility = View.GONE
        }
        loadMoreButton.setOnClickListener {
            currentPage += 1
            loadMoreResult(currentPage)
        }
    }

    override fun onResume() {
        super.onResume()
        var blogPage = 1
        var blogLimit = 20
        var blogList = findViewById<RecyclerView>(R.id.blogList)
        try {
            GlobalScope.launch(Dispatchers.IO) {
                val response =
                    retrofit.getBlog().getBlogs(blogPage, blogLimit).awaitResponse()
                // debug response
                Log.d("Response", response.toString())
                if (response.isSuccessful) {
                    val body = response.body()
                    launch(Dispatchers.Main) {
                        if (body != null) {
                            val blogAdapter = CustomBlogItem(body.data)

                            blogList!!.adapter = blogAdapter
                            blogList.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL)

                            blogAdapter.onItemClick = {
                                val intent = Intent(this@Home, BlogDetailActivity::class.java)
                                intent.putExtra("activity", "home")
                                intent.putExtra("blogId", it.id)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
    }

}



