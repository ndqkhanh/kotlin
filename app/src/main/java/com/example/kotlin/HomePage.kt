package com.example.kotlin

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import java.util.ArrayList

data class ListItemFormat(
    val id: String,
    val name: String
)
class HomePage : AppCompatActivity() {
    val retrofit = APIServiceImpl()
    lateinit var listBusStations: ArrayList<BusStation>
    lateinit var listBusOperators: ArrayList<BusOperator>
    lateinit var startPointEdit: EditText
    lateinit var endPointEdit: EditText
    lateinit var departureDateEdit: EditText

    var currentBusStartPoint = ""
    var currentBusEndPoint = ""

    private fun endPointEditHandle(){
        FragmentOperatorFilter().apply {
            // convert listBusOperators to listFilter
            listFilter = ArrayList()
            for (i in listBusStations.indices) {
                listFilter.add(ListItemFormat(listBusStations[i].id, listBusStations[i].name))
            }
            // pass listBusOperators to FragmentOperatorFilter
            arguments = Bundle().apply {
                putSerializable("listFilter", listFilter)
                putString("titleName", "Chọn nơi đến")
                if(currentBusEndPoint != ""){
                    putString("defaultId", currentBusEndPoint)
                }
            }
            show(supportFragmentManager, FragmentOperatorFilter.TAG)

            // onItemClick with data
            onItemClick = onItemClick@{item ->
                if(item.id == ""){
                    currentBusEndPoint = ""
                    endPointEdit.setText("")

                    return@onItemClick
                }
                currentBusEndPoint = item.id
                endPointEdit.setText(item.name)
            }
        }
    }

    private fun startPointEditHandle(){
        FragmentOperatorFilter().apply {
            // convert listBusOperators to listFilter
            listFilter = ArrayList()
            for (i in listBusStations.indices) {
                listFilter.add(ListItemFormat(listBusStations[i].id, listBusStations[i].name))
            }
            // pass listBusOperators to FragmentOperatorFilter
            arguments = Bundle().apply {
                putSerializable("listFilter", listFilter)
                putString("titleName", "Chọn nơi xuất phát")

                if(currentBusStartPoint != ""){
                    putString("defaultId", currentBusStartPoint)
                }
            }
            show(supportFragmentManager, FragmentOperatorFilter.TAG)

            // onItemClick with data
            onItemClick = onItemClick@{item ->
                if(item.id == ""){
                    currentBusStartPoint = ""
                    startPointEdit.setText("")

                    return@onItemClick
                }
                currentBusStartPoint = item.id
                startPointEdit.setText(item.name)
            }
        }
    }

    private fun handleSelectDate(){
        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            departureDateEdit.setText("$month/$dayOfMonth/$year")

        }, 2023, 3, 25).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val startPointSelect = findViewById<LinearLayout>(R.id.startPointSelect)
        startPointEdit = findViewById<EditText>(R.id.startPointEdit)
        // onClickListener for startPointSelect
        startPointSelect.setOnClickListener {
            startPointEditHandle()
        }

        // onClick startPointEdit
        startPointEdit.setOnClickListener {
            startPointEditHandle()
        }

        val endPointSelect = findViewById<LinearLayout>(R.id.endPointSelect)
        endPointEdit = findViewById<EditText>(R.id.endPointEdit)
        // onClickListener for startPointSelect
        endPointSelect.setOnClickListener {
            endPointEditHandle()
        }

        // onClick endPointEdit
        endPointEdit.setOnClickListener {
            endPointEditHandle()
        }

        GlobalScope.launch (Dispatchers.IO) {
            val response = retrofit.getAllBusStations().getBusStations().awaitResponse()
            if(response.isSuccessful){
                listBusStations = response.body()?.data as ArrayList<BusStation>
            }

            val response2 = retrofit.busOperatorService().getBusOperators().awaitResponse()
            if(response2.isSuccessful){
                listBusOperators = response2.body()?.data as ArrayList<BusOperator>
                // push all bus operator to list
                listBusOperators.add(0, BusOperator("all", "", "", "All"))

            }
        }

        val departureDate = findViewById<LinearLayout>(R.id.departureDate)
        departureDateEdit = findViewById<EditText>(R.id.departureDateEdit)
        // onClickListener for departureDate
        departureDate.setOnClickListener {
            handleSelectDate()
        }

        departureDateEdit.setOnClickListener {
            handleSelectDate()
        }

        val searchBus = findViewById<Button>(R.id.searchBus)

        searchBus.setOnClickListener {
            val intent = Intent(this, BusSearch::class.java)
            val departureId = listBusStations.find { it.name == startPointEdit.text.toString() }?.id
            val destinationId = listBusStations.find { it.name == endPointEdit.text.toString() }?.id
            val outputDateString = departureDateEdit.text.toString()
            val fromToString = startPointEdit.text.toString() + " -> " + endPointEdit.text.toString()
            intent.putExtra("departureId", departureId)
            intent.putExtra("destinationId", destinationId)
            intent.putExtra("outputDateString", outputDateString)
            intent.putExtra("fromToString", fromToString)
            startActivity(intent)
        }

        var blogPage = 1
        var blogLimit = 20
        var blogList = findViewById<RecyclerView>(R.id.news)
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
                            // using fragment_news_item.xml
//                            blogList.layoutManager = LinearLayoutManager(this@HomePage)

                            blogList.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL)

                            blogAdapter.onItemClick = {
//                                val intent = Intent(this@Home, BlogDetailActivity::class.java)
//                                intent.putExtra("activity", "home")
//                                intent.putExtra("blogId", it.id)
//                                startActivity(intent)
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