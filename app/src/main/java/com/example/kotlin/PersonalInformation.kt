package com.example.kotlin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.kotlin.jsonConvert.History
import com.facebook.login.widget.ProfilePictureView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse


class PersonalInformation : AppCompatActivity() {
    private lateinit var localEditor: SharedPreferences.Editor
    private val retrofit = APIServiceImpl()
    private var UserApi = retrofit.userService()
    private var page = 0
    private var limit = 3
    private var items = mutableListOf<History>()
    private lateinit var bookedTicketAdapter: BookedTicketAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_infor_ticket_history)

        var name = findViewById<TextView>(R.id.user_name)
        var email = findViewById<TextView>(R.id.email)
        var avt = findViewById<ProfilePictureView>(R.id.avt_user)
        var back = findViewById<ImageButton>(R.id.back_button)

        email.text = FBInfor.EMAIL
        name.text = FBInfor.NAME
        avt.profileId = FBInfor.ID

        var TicketHisListView = findViewById<ListView>(R.id.ticket_lv)

        bookedTicketAdapter = BookedTicketAdapter(this, items)
        TicketHisListView.adapter = bookedTicketAdapter
        loadData()

        TicketHisListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                    && TicketHisListView.lastVisiblePosition - TicketHisListView.headerViewsCount -
                    TicketHisListView.footerViewsCount >= bookedTicketAdapter.count - 1
                ) {
                    // Now your listview has hit the bottom
                    page += 1
                    loadData()
                }
            }
            override fun onScroll
                        (view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
            }
        })

        back.setOnClickListener {
            finish()
        }

    }
    private fun loadData(){
        GlobalScope.launch(Dispatchers.IO)   {
            withContext(Dispatchers.Main) {
                showLoadingGif()
            }
            val localStore = getSharedPreferences("vexere", Context.MODE_PRIVATE)
            localEditor = localStore.edit()
            var token: String? = localStore.getString("token", null)

            if (token != null) {
                val response = UserApi.ticketHistory("Bearer ${token}", page, limit).awaitResponse()
                if (response.isSuccessful) {

                    response.body()?.let {
                        var new_items = response.body()!!.history_list
                        if(new_items.isNotEmpty())
                            items.addAll(new_items)
                        else if(page > 0 )
                            page -= 1

                        withContext(Dispatchers.Main) {
                            bookedTicketAdapter.notifyDataSetChanged()
                        }
                    }

                }
            }
            withContext(Dispatchers.Main) {
                hideLoading()
            }
        }
    }
    private fun showLoadingGif(){
        var imageView = findViewById<ImageView>(R.id.loadingGif)
        imageView.visibility = VISIBLE
        Glide.with(this).load(R.drawable.loading).into(imageView)
    }
    private fun hideLoading(){
        var imageView = findViewById<ImageView>(R.id.loadingGif)
        imageView.visibility = INVISIBLE
    }
}