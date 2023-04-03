package com.example.kotlin

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
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

        val adapter = CustomTicketItem(this, busses, supportFragmentManager, lifecycle)

        adapter.showBottomSheet = {
//            val dialog = BottomSheetDialog(this@Home)
//            val view = layoutInflater.inflate(R.layout.activity_bus_detail, null)
//            val busAdapter = BusDetailAdapter(supportFragmentManager, lifecycle)
//            val pagerBus = view.findViewById<ViewPager2>(R.id.pagerBus)
//            val tabLayout = view.findViewById<com.google.android.material.tabs.TabLayout>(R.id.tabLayout)
//            pagerBus.adapter = busAdapter
//            TabLayoutMediator(tabLayout, pagerBus) { tab, position ->
//                when (position) {
//                    0 -> tab.text = "Bus Operator"
//                    1 -> tab.text = "Bus Information"
//                }
//            }.attach()
//
//            dialog.setCancelable(false)
//
//            dialog.setContentView(view)
//
//            dialog.show()
        }
        searchResult!!.adapter = adapter

        var blogs = ArrayList<Blog>()
        blogs.add(Blog("Ninh bình 2", "https://cdn.pixabay.com/photo/2015/10/01/17/17/car-967387__480.png", "1h 30p"))
        blogs.add( Blog("Ninh bình 3", "https://cdn.pixabay.com/photo/2015/10/01/17/17/car-967387__480.png", "1h 30p"))

        var blogList = findViewById<RecyclerView>(R.id.blogList)

        val blogAdapter = CustomBlogItem(blogs)

        blogList!!.adapter = blogAdapter
        blogList!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

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