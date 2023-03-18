package com.example.kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator

class BusDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_detail)

        val busAdapter = BusDetailAdapter(supportFragmentManager, lifecycle)
        val pagerBus = findViewById<ViewPager2>(R.id.pagerBus)
        val tabLayout = findViewById<com.google.android.material.tabs.TabLayout>(R.id.tabLayout)
        pagerBus.adapter = busAdapter
        TabLayoutMediator(tabLayout, pagerBus) { tab, position ->
            when (position) {
                0 -> tab.text = "Bus Operator"
                1 -> tab.text = "Bus Information"
            }
        }.attach()
    }
}