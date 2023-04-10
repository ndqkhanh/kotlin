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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse


class Home : AppCompatActivity() {
    val retrofit = APIServiceImpl()
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
        findViewById<ImageButton>(R.id.person_infor).setOnClickListener{
            val intent = Intent(this, PersonalInformation::class.java)
            startActivity(intent)
        }

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