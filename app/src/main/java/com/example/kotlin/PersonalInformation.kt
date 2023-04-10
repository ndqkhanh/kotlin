package com.example.kotlin

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import com.example.kotlin.jsonConvert.History
import com.facebook.login.widget.ProfilePictureView


class PersonalInformation : AppCompatActivity() {
    private lateinit var localEditor: SharedPreferences.Editor
    private val retrofit = APIServiceImpl()
    private var UserApi = retrofit.userService()

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
        back.setOnClickListener {
            finish()
        }

        val localStore = getSharedPreferences("vexere", Context.MODE_PRIVATE)
        localEditor = localStore.edit()
        var token: String? = localStore.getString("token", null)

        var TicketHisListView = findViewById<ListView>(R.id.ticket_lv)

        var items = listOf<History>(History(), History(),History())
        var bookedTicketAdapter = BookedTicketAdapter(this, items)
        TicketHisListView.adapter = bookedTicketAdapter


//        GlobalScope.launch(Dispatchers.IO) {
//            //check valid token
//            var validToken: Boolean = false
//            if (token != null) {
//
//                val response = UserApi.ticketHistory("Bearer ${token}", 0, 3).awaitResponse()
//                if (response.isSuccessful) {
//                    validToken = true
//                }
//            }
//        }

    }
}