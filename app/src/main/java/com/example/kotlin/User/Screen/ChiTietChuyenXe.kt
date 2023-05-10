package com.example.kotlin.User.Screen

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TabHost
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kotlin.APIServiceImpl
import com.example.kotlin.Adapter.ReviewAdapter
import com.example.kotlin.DataClass.*
import com.example.kotlin.R
import com.example.kotlin.utils.UserInformation
import com.example.kotlin.utils.WaitingAsyncClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChiTietChuyenXe: AppCompatActivity() {
    private lateinit var tabHost: TabHost
    private lateinit var tabSpec1: TabHost.TabSpec
    private lateinit var average_star: TextView
    private lateinit var rcv: RecyclerView
    private lateinit var srl_review: SwipeRefreshLayout
    private lateinit var serviceStar: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var back: ImageButton
    private var page = 0
    private var limit = 10
    private var reviewList = mutableListOf<ReviewItem>()
    private var reviewAdapter = ReviewAdapter(reviewList)
    private var boId: String? = null

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
        srl_review = findViewById(R.id.srl_review)
        serviceStar = findViewById(R.id.service_star)
        progressBar = findViewById(R.id.progressBar_chat_luong_dv)
        back = findViewById(R.id.chi_tiet_ve_back)

        srl_review.setOnRefreshListener {
            callData()
            startGetReview()
            srl_review.isRefreshing = false
        }

        back.setOnClickListener { finish() }
        rcv.layoutManager = LinearLayoutManager(this)
        rcv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        val dialog = ProgressDialog(this)
        dialog.setCancelable(false)

        boId = intent.getStringExtra("boId")
        if(boId == null)
            finish()

        callData()
        startGetReview()
    }

    private fun callData(){
        var callAvg = APIServiceImpl.busOperatorService().getAverageRating(boId!!)
        callAvg.enqueue(object : Callback<AverageRating> {
            override fun onResponse(call: Call<AverageRating>, response: Response<AverageRating>) {
                if(response.body() != null){
                    average_star.text = response.body()!!.avg.toString()
                    serviceStar.text = response.body()!!.avg.toString()
                    progressBar.progress = (response.body()!!.avg * 100).toInt()
                }
            }

            override fun onFailure(call: Call<AverageRating>, t: Throwable) {
                //TODO Nothing
            }
        })
    }

    private fun startGetReview(){
        page = 0
        reviewList.clear()
        rcv.adapter = reviewAdapter
        loadReview()
        rcv.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(view: RecyclerView, scrollState: Int) {
                super.onScrollStateChanged(view, scrollState)
                if(!view.canScrollVertically(1) && scrollState == RecyclerView.SCROLL_STATE_IDLE){
                    page += 1
                    loadReview()
                }
            }
        })
    }

    private fun loadReview(){
        val callReviews = APIServiceImpl.busOperatorService().getReviews(boId!!,page, limit)

        callReviews.enqueue(object : Callback<ReviewList>{
            override fun onResponse(call: Call<ReviewList>, response: Response<ReviewList>) {
                if(response.body() != null){
                    val res = response.body()!!
                    if(res.review_list.isNotEmpty()) {
                        reviewList.addAll(res.review_list)
                        reviewAdapter.notifyDataSetChanged()
                    }else{
                        if(page > 0) page -= 1}
                }
            }

            override fun onFailure(call: Call<ReviewList>, t: Throwable) {
                if(page > 0) page -= 1
            }
        })
    }
}