package com.example.kotlin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
//                    val busInfoRV = findViewById<RecyclerView>(R.id.busInfoRV)
//                    var texts = ArrayList<String>()
//                    texts.add("Bus Operator Name")
//                    texts.add(bus?.bus_operators?.name ?: "")
//                    texts.add("Bus Operator Phone")
//                    texts.add(bus?.bus_operators?.phone ?: "")
//                    val busAdapter = CustomBusDataItem(texts)
//                    busInfoRV!!.layoutManager = GridLayoutManager(inflater.context, 2)
//                    busInfoRV!!.adapter = busAdapter
                    Log.d("Search", "test" + view.toString())
                    Log.d("Search", "test" + view.transitionName)

//                    val busInfoTitle = findViewById<TextView>(R.id.busInfoTitle)
//                    busInfoTitle.text = "Bus Information 2"
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
//                    val busInfoRV = findViewById<RecyclerView>(R.id.busInfoRV)
//                    var texts = ArrayList<String>()
//                    texts.add("Bus Operator Name")
//                    texts.add(bus?.bus_operators?.name ?: "")
//                    texts.add("Bus Operator Phone")
//                    texts.add(bus?.bus_operators?.phone ?: "")
//
//                    val blogAdapter = CustomBusDataItem(texts)
//                    Log.d("Search", "busInfoRV: " + busInfoRV.toString())
//                    Log.d("Search", texts.toString())
//                    Log.d("Search", "test: " + blogAdapter.toString())
//                    busInfoRV!!.adapter = blogAdapter
//                    busInfoRV!!.layoutManager = GridLayoutManager(this@BusDetailActivity, 2)
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