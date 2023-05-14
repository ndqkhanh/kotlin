package com.example.kotlin.User.Screen

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TabHost
import android.widget.TabHost.TabSpec
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.kotlin.APIServiceImpl
import com.example.kotlin.Adapter.PointAdapter
import com.example.kotlin.Adapter.ReviewAdapter
import com.example.kotlin.DataClass.*
import com.example.kotlin.R
import com.example.kotlin.utils.UserInformation
import com.example.kotlin.utils.WaitingAsyncClass
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChiTietChuyenXe: AppCompatActivity() {
    private lateinit var tabHost: TabHost
    private lateinit var tabSpec2: TabSpec
    private lateinit var tabSpec1: TabSpec
    private lateinit var tabSpec3: TabSpec
    private lateinit var tabSpec4: TabSpec

    //review
    private lateinit var average_star: TextView
    private lateinit var rcv: RecyclerView
    private lateinit var srl_review: SwipeRefreshLayout
    private lateinit var serviceStar: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var back: ImageButton
    private lateinit var themBinhLuan: TextView

    //don/tra
    private lateinit var btnDon: TextView
    private lateinit var btnTra: TextView
    private lateinit var rcvDon: RecyclerView
    private lateinit var rcvTra: RecyclerView
    private var donList = mutableListOf<Point>()
    private var traList = mutableListOf<Point>()
    private var donAdapter = PointAdapter(donList)
    private var traAdapter = PointAdapter(traList)

    //hinh anh
    private lateinit var hinhAnhNhaXe: ImageView
    private lateinit var hinhAnhXe: ImageView

    //chinh sach
    private lateinit var sdt: TextView
    private lateinit var chinhSach: TextView
    private lateinit var boName: TextView
    private lateinit var thoiGianDuoiBo: TextView

    private var page = 0
    private var limit = 10
    private var reviewList = mutableListOf<ReviewItem>()
    private var reviewAdapter = ReviewAdapter(reviewList)
    private var boId: String? = null
    private var bId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bus_detail)

        tabHost = findViewById(R.id.busDetailTabHost)
        tabHost.setup()

        tabSpec1 = tabHost.newTabSpec("t1")
        tabSpec1.setContent(R.id.tab1)
        tabSpec1.setIndicator("Đón/Trả")
        tabHost.addTab(tabSpec1)

        tabSpec2 = tabHost.newTabSpec("t2")
        tabSpec2.setContent(R.id.tab2)
        tabSpec2.setIndicator("Đánh giá")
        tabHost.addTab(tabSpec2)

        tabSpec3 = tabHost.newTabSpec("t3")
        tabSpec3.setContent(R.id.tab3)
        tabSpec3.setIndicator("Hình ảnh")
        tabHost.addTab(tabSpec3)

        tabSpec4 = tabHost.newTabSpec("t4")
        tabSpec4.setContent(R.id.tab4)
        tabSpec4.setIndicator("Chính sách")
        tabHost.addTab(tabSpec4)

        boName = findViewById(R.id.bo_name)
        thoiGianDuoiBo = findViewById(R.id.thoi_gian_tren_bo)

        //review
        average_star = findViewById(R.id.average_star)
        rcv = findViewById(R.id.rcv_chi_tiet_danh_gia)
        srl_review = findViewById(R.id.srl_review)
        serviceStar = findViewById(R.id.service_star)
        progressBar = findViewById(R.id.progressBar_chat_luong_dv)
        back = findViewById(R.id.chi_tiet_ve_back)
        themBinhLuan = findViewById(R.id.them_binh_luan)

        //don/tra
        btnDon = findViewById(R.id.btn_diem_don)
        btnTra = findViewById(R.id.btn_diem_tra)
        rcvDon = findViewById(R.id.rcv_don)
        rcvTra = findViewById(R.id.rcv_tra)
        rcvDon.layoutManager = LinearLayoutManager(this)
        rcvTra.layoutManager = LinearLayoutManager(this)
        rcvDon.adapter = donAdapter
        rcvTra.adapter = traAdapter

        //hinh anh
        hinhAnhNhaXe = findViewById(R.id.hinh_anh_nha_xe)
        hinhAnhXe = findViewById(R.id.hinh_anh_xe)

        //chinh sach
        sdt = findViewById(R.id.sdt_nha_xe)
        chinhSach = findViewById(R.id.chinh_sach_nha_xe)

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
        bId = intent.getStringExtra("bId")

        if(boId == null || bId == null)
            finish()

        callData()
        startGetReview()

        themBinhLuan.setOnClickListener{
            if(UserInformation.TOKEN != null) {
                val intentCreateReview = Intent(this, ThemBinhLuan::class.java)
                intentCreateReview.putExtra("boId", boId)
                startActivity(intentCreateReview)
            }
        }
        btnDon.setOnClickListener {
            rcvDon.visibility = VISIBLE
            rcvTra.visibility = GONE
            btnDon.setTextColor(btnDon.resources.getColor(R.color.black))
            btnTra.setTextColor(btnTra.resources.getColor(R.color.gray_bold_text))
        }
        btnTra.setOnClickListener{
            rcvTra.visibility = VISIBLE
            rcvDon.visibility = GONE
            btnTra.setTextColor(btnDon.resources.getColor(R.color.black))
            btnDon.setTextColor(btnTra.resources.getColor(R.color.gray_bold_text))
        }
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

        val callBusDetail = APIServiceImpl.busService().getBusDetail(bId!!)
        callBusDetail.enqueue(object:  Callback<BusDetail>{
            override fun onResponse(call: Call<BusDetail>, response: Response<BusDetail>) {
                if(response.body() != null){
                    var item = response.body()!!

                    boName.text = item.ten_nha_xe + " bus"
                    thoiGianDuoiBo.text =  "${item.start_time} • ${item.start_date}"
                    //don/tra
                    donList.clear()
                    donList.addAll(item.ds_don)
                    btnDon.text = "Điểm đón (${donList.size})"
                    donAdapter.notifyDataSetChanged()
                    traList.clear()
                    traList.addAll(item.ds_tra)
                    btnTra.text = "Điểm trả (${traList.size})"
                    traAdapter.notifyDataSetChanged()

                    //hinh anh
                    Glide.with(this@ChiTietChuyenXe).load(item.anh_nha_xe).into(hinhAnhNhaXe)
                    Glide.with(this@ChiTietChuyenXe).load(item.anh_xe).into(hinhAnhXe)

                    //chinh sach
                    sdt.text = item.sdt_nha_xe
                    chinhSach.text = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                         Html.fromHtml(item.policy, Html.FROM_HTML_MODE_COMPACT)
                    }else{
                        Html.fromHtml(item.policy)
                    }
                }
            }

            override fun onFailure(call: Call<BusDetail>, t: Throwable) {
                finish()
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