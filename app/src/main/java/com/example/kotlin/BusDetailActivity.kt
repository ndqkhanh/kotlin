package com.example.kotlin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import java.util.*


class BusDetailActivity : AppCompatActivity() {
    var bus: Bus? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_detail)
        val closeBtn = findViewById<Button>(R.id.closeBtn)
        // close activity
        closeBtn.setOnClickListener {
            finish()
        }

        val busAdapter = BusDetailAdapter(supportFragmentManager, lifecycle)
        val pagerBus = findViewById<ViewPager2>(R.id.pagerBus)
        val tabLayout = findViewById<com.google.android.material.tabs.TabLayout>(R.id.tabLayout)
        // detect onClick tabLayout
        tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                Log.d("Search", "onTabSelected: " + tab?.position)
                if (tab?.position == 1) {
                    // get inflater
                    val inflater = LayoutInflater.from(this@BusDetailActivity)
                    val view: View = inflater.inflate(R.layout.fragment_bus_information, null)

                }
            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                Log.d("Search", "onTabUnselected: " + tab?.position)
            }

            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                Log.d("Search", "onTabReselected: " + tab?.position)
            }

        })


        pagerBus.adapter = busAdapter


        TabLayoutMediator(tabLayout, pagerBus) { tab, position ->
            when (position) {
                0 -> tab.text = "Bus Operator"
                1 -> {
                    tab.text = "Bus Information"
                }
            }
        }.attach()
        val busId = intent.getStringExtra("busId") ?: ""
        // call API to get bus detail by id (getBusById)
        GlobalScope.launch (Dispatchers.IO) {
            val response = APIServiceImpl()?.bus()?.getBusById(busId)?.awaitResponse()
            if (response.isSuccessful) {
                Log.d("Search", response.body().toString())
                var data = response.body()
                bus = data

                val arr = ArrayList<String>()
                arr.add("Bus operator")
                arr.add(data?.bus_operators?.name ?: "")
                arr.add("Start point")
                arr.add(data?.start_point?.name ?: "")
                arr.add("End point")
                arr.add(data?.end_point?.name ?: "")
                arr.add("Start time")
                arr.add(data?.start_time ?: "")
                arr.add("End time")
                arr.add(data?.end_time ?: "")
                arr.add("Number of seat")
                arr.add(data?.num_of_seats.toString() ?: "")
                arr.add("Type of bus")
                arr.add(data?.type.toString() ?: "")
                arr.add("Price")
                arr.add(data?.pricing_format ?: "")

                // put arr in sharedPreference
                val sharePref = getSharedPreferences("BusDetail", MODE_PRIVATE)
                val editor = sharePref.edit()
                val str = arr.joinToString(";")
                Log.d("Search", "str: $str")
                editor.putString("busInformation", str)
                editor.putString("busAvatar", data?.image_url)
                editor.apply()

                withContext(Dispatchers.Main) {
                    val busOperatorName = findViewById<TextView>(R.id.busOperatorName)
                    val busOperatorPhone = findViewById<TextView>(R.id.busOperatorPhone)
                    val busOperatorAvatar = findViewById<ImageView>(R.id.busOperatorAvatar)

                    busOperatorName.text = data?.bus_operators?.name
                    busOperatorPhone.text = data?.bus_operators?.phone

                    Glide.with(busOperatorAvatar.context)
                        .load(data?.bus_operators?.image_url)
                        .into(busOperatorAvatar)

                    val busInfoTitle = findViewById<TextView>(R.id.busInfoTitle)
                    Log.d("Search", "busInfoTitle: " + busInfoTitle.toString())

                }
            }
        }
    }
}