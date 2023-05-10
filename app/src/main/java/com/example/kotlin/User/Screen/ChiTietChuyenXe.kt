package com.example.kotlin.User.Screen

import android.os.Bundle
import android.widget.TabHost
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin.R

class ChiTietChuyenXe: AppCompatActivity() {
    private lateinit var tabHost: TabHost
    private lateinit var tabSpec1: TabHost.TabSpec
    private lateinit var average_star: TextView
    private lateinit var rcv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bus_detail)

        tabHost = findViewById(R.id.busDetailTabHost)
        tabHost.setup()

        tabSpec1 = tabHost.newTabSpec("t1")
        tabSpec1.setContent(R.id.tab1)
        tabSpec1.setIndicator("Đánh giá nhà xe")
        tabHost.addTab(tabSpec1)

        average_star = findViewById(R.id.average_star)
        rcv = findViewById(R.id.rcv_chi_tiet_danh_gia)




    }
}