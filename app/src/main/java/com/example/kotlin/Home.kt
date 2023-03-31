package com.example.kotlin

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.login.LoginManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import java.util.*


class Home : AppCompatActivity() {
    private lateinit var localEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val localStore = getSharedPreferences("vexere", Context.MODE_PRIVATE)
        localEditor = localStore.edit()

        findViewById<LinearLayout>(R.id.home_logout_button).setOnClickListener{
            Log.d("Response", "Đăng xuất")
            localEditor.apply {
                putString("token",".")
                commit()
            }// remove token
            Firebase.auth.signOut()
            LoginManager.getInstance().logOut()
            finish()
        }

        val tickets = ArrayList<Ticket>()
        tickets.add(Ticket("Ninh bình 2", "100.000đ", "1h 30p", "Hà Nội", "HCM"))
        tickets.add( Ticket("Ninh bình 1", "100.000đ", "1h 30p", "Hà Nội", "HCM"))

        var searchResult = findViewById<ListView>(R.id.searchResult)

        val adapter = CustomTicketItem(this, tickets)

        searchResult!!.adapter = adapter

        var blogs = ArrayList<Blog>()
        blogs.add(Blog("Ninh bình 2", "https://cdn.pixabay.com/photo/2015/10/01/17/17/car-967387__480.png", "1h 30p"))
        blogs.add( Blog("Ninh bình 3", "https://cdn.pixabay.com/photo/2015/10/01/17/17/car-967387__480.png", "1h 30p"))

        var blogList = findViewById<RecyclerView>(R.id.blogList)

        val blogAdapter = CustomBlogItem(blogs)

        blogList!!.adapter = blogAdapter
        blogList!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        Utility.setListViewHeightBasedOnChildren(searchResult)

        var datePicker = findViewById<EditText>(R.id.datePicker)

        // onCLick datePicker
        datePicker.setOnClickListener {
//            val datePickerFragment = DatePickerFragment()
//            datePickerFragment.show(supportFragmentManager, "datePicker")
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                Log.d("Date", "$dayOfMonth/$month/$year")
            }, 2020, 1, 1).show()
        }


        val retrofit = APIServiceImpl()

        GlobalScope.launch (Dispatchers.IO) {
            val response = retrofit.getAllBusStations().getBusStations().awaitResponse()
            Log.d("Response", "vui 1" + response.message())
            // debug response
            Log.d("Response", response.toString())
            if(response.isSuccessful){
                Log.d("Response", "vui 2")
                val data = response.body()?.data
                Log.d("Response", data.toString())
            }
        }
    }

}