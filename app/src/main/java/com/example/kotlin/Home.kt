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
import android.view.View.VISIBLE
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
    lateinit var listBusStations: List<BusStation>
    lateinit var spinnerDeparture: Spinner
    lateinit var spinnerDestination: Spinner
    lateinit var datePicker: EditText
    lateinit var searchResult: ListView
    lateinit var loadMoreButton: Button
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
                val response = APIServiceImpl().searchBusses().search(BusSearchRequest(departureId, destinationId, page, limit, outputDateString)).awaitResponse()
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

        GlobalScope.launch (Dispatchers.IO) {
            val response = retrofit.getAllBusStations().getBusStations().awaitResponse()
            if(response.isSuccessful){
                listBusStations = response.body()?.data as List<BusStation>

                withContext(Dispatchers.Main) {
                    spinnerDeparture!!.adapter = ArrayAdapter(this@Home, android.R.layout.simple_list_item_single_choice, listBusStations.map { it.name })
                    spinnerDestination!!.adapter = ArrayAdapter(this@Home, android.R.layout.simple_list_item_single_choice, listBusStations.map { it.name })
                }
            }
        }

        val busOperatorFilter = findViewById<Spinner>(R.id.busOperatorFilter)

        val searchButton = findViewById<Button>(R.id.searchButton)

        searchButton.setOnClickListener {
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

}



