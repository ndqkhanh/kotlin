package com.example.kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.facebook.Profile
import com.facebook.login.widget.ProfilePictureView



class PersonalInformation : AppCompatActivity() {

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

    }
}