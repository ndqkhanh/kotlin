package com.example.kotlin

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse


class Home : AppCompatActivity() {
    private lateinit var localEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

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

        val tickets = ArrayList<Ticket>()
        tickets.add(
            Ticket(
                "Ninh bình 2",
                "100.000đ",
                "1h 30p",
                "Hà Nội",
                "HCM",
                "32bd7781-0713-45cc-9841-0abb62a807e0"
            )
        )
        tickets.add(
            Ticket(
                "Ninh bình 1",
                "100.000đ",
                "1h 30p",
                "Hà Nội",
                "HCM",
                "384fdcb1-496f-4f87-8b1e-578674111ac1"
            )
        )

        var searchResult = findViewById<ListView>(R.id.searchResult)

        val adapter = CustomTicketItem(this, tickets)

        searchResult!!.adapter = adapter

        var blogs = ArrayList<Blog>()
        blogs.add(
            Blog(
                "Ninh bình 2",
                "https://cdn.pixabay.com/photo/2015/10/01/17/17/car-967387__480.png",
                "9dacf3c6-f73e-4218-a01d-097d8d3a7a20"
            )
        )
        blogs.add(
            Blog(
                "Ninh bình 3",
                "https://cdn.pixabay.com/photo/2015/10/01/17/17/car-967387__480.png",
                "310336cc-4250-4759-b182-4913d86af5c2"
            )
        )

        var blogList = findViewById<RecyclerView>(R.id.blogList)

        val blogAdapter = CustomBlogItem(blogs)

        blogList!!.adapter = blogAdapter
        blogList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        blogAdapter.onItemClick = {
            val intent = Intent(this, BlogDetailActivity::class.java)
            intent.putExtra("blogId", it.id)
            startActivity(intent)
        }

        Utility.setListViewHeightBasedOnChildren(searchResult)

        var datePicker = findViewById<EditText>(R.id.datePicker)

        // onCLick datePicker
        datePicker.setOnClickListener {
//            val datePickerFragment = DatePickerFragment()
//            datePickerFragment.show(supportFragmentManager, "datePicker")
            DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    Log.d("Date", "$dayOfMonth/$month/$year")
                },
                2020,
                1,
                1
            ).show()
        }


        val retrofit = APIServiceImpl()

        GlobalScope.launch(Dispatchers.IO) {
            val response = retrofit.getAllBusStations().getBusStations().awaitResponse()
            Log.d("Response", "vui 1" + response.message())
            // debug response
            Log.d("Response", response.toString())
            if (response.isSuccessful) {
                Log.d("Response", "vui 2")
                val data = response.body()?.data
                Log.d("Response", data.toString())
            }
        }
    }

}