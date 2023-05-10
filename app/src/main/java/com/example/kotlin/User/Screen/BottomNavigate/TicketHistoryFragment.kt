package com.example.kotlin.User.Screen.BottomNavigate

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TabHost
import android.widget.TabHost.TabSpec
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kotlin.APIServiceImpl
import com.example.kotlin.R
import com.example.kotlin.Adapter.TicketHistoryAdapter
import com.example.kotlin.User.Screen.BottomNavigate.TicketHistory.ThongTinVeAcivity
import com.example.kotlin.Widget.UserInformation
import com.example.kotlin.Widget.WaitingAsyncClass
import com.example.kotlin.DataClass.HistoryItem
import com.example.kotlin.DataClass.HistoryList
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
    private val UserAPI = APIServiceImpl.userService()
    private lateinit var veHienTai: RecyclerView
    private lateinit var veHuy: RecyclerView
    private lateinit var veDaDi: RecyclerView
    private var veHT: MutableList<HistoryItem> = mutableListOf()
    private var veH: MutableList<HistoryItem> = mutableListOf()
    private var veD: MutableList<HistoryItem> = mutableListOf()
    private var veHTAdapter = TicketHistoryAdapter(0, veHT)
    private var veHAdapter = TicketHistoryAdapter(1, veH)
    private val veDAdapter = TicketHistoryAdapter(2, veD)
    private lateinit var srl_HT: SwipeRefreshLayout
    private lateinit var srl_H: SwipeRefreshLayout
    private lateinit var srl_D: SwipeRefreshLayout
    private var ticketAPI = APIServiceImpl.ticketService()

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

        srl_HT = root.findViewById(R.id.srl_ve_hien_tai)
        srl_D = root.findViewById(R.id.srl_ve_da_di)
        srl_H = root.findViewById(R.id.srl_ve_huy)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dialog = ProgressDialog(requireContext())
        dialog.setCancelable(false)

        var itemEvent: (HistoryItem)->Unit = { item->
            val intentCTVe = Intent(requireActivity(), ThongTinVeAcivity::class.java)
            intentCTVe.putExtra("item", item)
            startActivity(intentCTVe)
        }
        var discardEvent: (HistoryItem, Int) -> Unit = { item, id->
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Hủy vé").setMessage("Lưu ý rằng khi hủy vé, bạn sẽ không được hoàn tiền")
            .setPositiveButton("Hủy vé") { dialogInterface, i ->
                val callDiscard: Call<HistoryItem> = ticketAPI.discardTicket(item.id, "Bearer ${UserInformation.TOKEN!!}")
                dialog.show()
                var respone: HistoryItem? = WaitingAsyncClass(callDiscard).execute().get()
                dialog.dismiss()
                if(respone != null) {
                    veHT.removeAt(id)
                    veHTAdapter.notifyItemRemoved(id)
                    startDiscard()
                }else builder.setMessage("Hủy vé thất bại")

            }
            .setNegativeButton("Đóng"
            ) { dialogInterface, i ->
                dialogInterface.cancel()
            }
            val alert: AlertDialog = builder.create()
            alert.show()
        }
        veHTAdapter.onItemClick = itemEvent
        veHAdapter.onItemClick = itemEvent
        veDAdapter.onItemClick = itemEvent

        veHTAdapter.onDiscard = discardEvent

        if(UserInformation.TOKEN != null){
            startCurrent()
            startDone()
            startDiscard()
            srl_HT.setOnRefreshListener {
                startCurrent()
                srl_HT.isRefreshing = false
            }
            srl_D.setOnRefreshListener {
                startDone()
                srl_D.isRefreshing = false
            }
            srl_H.setOnRefreshListener {
                startDiscard()
                srl_H.isRefreshing = false
            }
        }

    }
    private fun startCurrent(){
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
    }
    private fun startDone(){
        pageD = 0
        veD.clear()
        veDaDi.adapter = veDAdapter
        loadDone()
        veDaDi.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(view: RecyclerView, scrollState: Int) {
                super.onScrollStateChanged(view, scrollState)
                if(!view.canScrollVertically(1) && scrollState == RecyclerView.SCROLL_STATE_IDLE){
                    pageD += 1
                    loadDone()
                }
            }
        })
    }
    private fun startDiscard(){
        pageH = 0
        veH.clear()
        veHuy.adapter = veHAdapter
        loadDiscard()
        veHuy.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(view: RecyclerView, scrollState: Int) {
                super.onScrollStateChanged(view, scrollState)
                if(!view.canScrollVertically(1) && scrollState == RecyclerView.SCROLL_STATE_IDLE){
                    pageH += 1
                    loadDiscard()
                }
            }
        })

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
                if(pageHT > 0) pageHT -= 1
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
                if(pageD > 0)
                    pageD -= 1
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
                if(pageH > 0)
                    pageH -= 1
            }
        })
    }

}
