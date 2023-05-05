package com.example.kotlin.User.Screen.BottomNavigate.TicketHistory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TabHost
import android.widget.TabHost.TabSpec
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin.APIServiceImpl
import com.example.kotlin.R
import com.example.kotlin.UserInformation
import com.example.kotlin.WaitingAsyncClass
import com.example.kotlin.jsonConvert.History
import com.example.kotlin.jsonConvert.HistoryList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TicketHistoryFragment : Fragment() {
    private lateinit var tabHost: TabHost
    private lateinit var tabSpec1: TabSpec
    private lateinit var tabSpec2: TabSpec
    private lateinit var tabSpec3: TabSpec
    private var limit = 3
    private var pageHT = 0
    private var pageD = 0
    private var pageH = 0
    private val UserAPI = APIServiceImpl().userService()
    private lateinit var veHienTai: RecyclerView
    private lateinit var veHuy: RecyclerView
    private lateinit var veDaDi: RecyclerView
    private var veHT: MutableList<History> = mutableListOf()
    private var veH: MutableList<History> = mutableListOf()
    private var veD: MutableList<History> = mutableListOf()
    private var veHTAdapter = TicketHistoryAdapter(0, veHT)
    private var veHAdapter = TicketHistoryAdapter(1, veH)
    private val veDAdapter = TicketHistoryAdapter(2, veD)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_ticket_history, container, false)
        tabHost = root.findViewById(R.id.tabHostVe)
        tabHost.setup()

        tabSpec1 = tabHost.newTabSpec("t1")
        tabSpec1.setContent(R.id.tab1)
        tabSpec1.setIndicator("Vé hiện tại")
        tabHost.addTab(tabSpec1)

        tabSpec2 = tabHost.newTabSpec("t2")
        tabSpec2.setContent(R.id.tab2)
        tabSpec2.setIndicator("Vé đã đi")
        tabHost.addTab(tabSpec2)

        tabSpec3 = tabHost.newTabSpec("t3")
        tabSpec3.setContent(R.id.tab3)
        tabSpec3.setIndicator("Vé đã hủy")
        tabHost.addTab(tabSpec3)

        veHienTai = root.findViewById(R.id.rclView_ve_hien_tai)
        veHienTai.layoutManager = LinearLayoutManager(requireActivity())
        veHuy = root.findViewById(R.id.rclView_ve_huy)
        veHuy.layoutManager = LinearLayoutManager(requireActivity())
        veDaDi = root.findViewById(R.id.rclView_ve_da_di)
        veDaDi.layoutManager = LinearLayoutManager(requireActivity())

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        if(UserInformation.TOKEN != null){
        pageHT = 0
        veHT.clear()
        veHienTai.adapter = veHTAdapter
        loadCurrent()
        veHienTai.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(view: RecyclerView, scrollState: Int) {
                super.onScrollStateChanged(view, scrollState)
                if(!view.canScrollVertically(1) && scrollState == RecyclerView.SCROLL_STATE_IDLE){
                    pageHT += 1
                    loadCurrent()
                }
            }
        })

        pageD = 0
        veD.clear()
        veDaDi.adapter = veDAdapter
        loadDone()
        veDaDi.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(view: RecyclerView, scrollState: Int) {
                super.onScrollStateChanged(view, scrollState)
                if(!view.canScrollVertically(1) && scrollState == RecyclerView.SCROLL_STATE_IDLE){
                    pageHT += 1
                    loadDone()
                }
            }
        })

        pageH = 0
        veH.clear()
        veHuy.adapter = veHAdapter
        loadDiscard()
        veHuy.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(view: RecyclerView, scrollState: Int) {
                super.onScrollStateChanged(view, scrollState)
                if(!view.canScrollVertically(1) && scrollState == RecyclerView.SCROLL_STATE_IDLE){
                    pageHT += 1
                    loadDiscard()
                }
            }
        })
        }

    }
    private fun loadCurrent(){

        var callCurrent: Call<HistoryList> = UserAPI.ticketHistory("Bearer ${UserInformation.TOKEN!!}",pageHT,limit, "current")
        callCurrent.enqueue(object : Callback<HistoryList>{
            override fun onResponse(call: Call<HistoryList>, response: Response<HistoryList>) {
                if(response?.body() != null){
                    val res = response.body()!!
                    if(res.history_list.isNotEmpty()) {
                        veHT.addAll(res.history_list)
                        veHTAdapter.notifyDataSetChanged()
                    }else if(pageHT > 0) pageHT -= 1

                }
            }

            override fun onFailure(call: Call<HistoryList>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun loadDone(){
        var callDone: Call<HistoryList> = UserAPI.ticketHistory("Bearer ${UserInformation.TOKEN!!}",pageD,limit, "done")
        callDone.enqueue(object : Callback<HistoryList>{
            override fun onResponse(call: Call<HistoryList>, response: Response<HistoryList>) {
                if(response?.body() != null){
                    val res = response.body()!!
                    if(res.history_list.isNotEmpty()){
                        veD.addAll(res.history_list)
                        veDAdapter.notifyDataSetChanged()
                    }else if(pageD > 0)
                        pageD -= 1
                }
            }

            override fun onFailure(call: Call<HistoryList>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun loadDiscard(){
        var callDiscard: Call<HistoryList> = UserAPI.ticketHistory("Bearer ${UserInformation.TOKEN!!}",pageH,limit, "discard")
        callDiscard.enqueue(object : Callback<HistoryList>{
            override fun onResponse(call: Call<HistoryList>, response: Response<HistoryList>) {
                if(response?.body() != null){
                    val res = response.body()!!
                    if(res.history_list.isNotEmpty()) {
                        veH.addAll(res.history_list)
                        veHAdapter.notifyDataSetChanged()
                    }else if(pageH > 0)
                        pageH -= 1
                }
            }

            override fun onFailure(call: Call<HistoryList>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

}
