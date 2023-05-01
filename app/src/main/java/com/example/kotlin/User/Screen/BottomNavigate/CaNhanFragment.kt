package com.example.kotlin.User.Screen.BottomNavigate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.kotlin.LogInUp
import com.example.kotlin.R
import com.example.kotlin.UserInformation
import com.example.kotlin.jsonConvert.User
import android.content.SharedPreferences
import com.facebook.login.LoginManager
import com.facebook.login.widget.ProfilePictureView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CaNhanFragment : Fragment() {
    private lateinit var loginBtn: TextView
    private lateinit var username: TextView
    private lateinit var email: TextView
    private lateinit var logout: LinearLayout
    private lateinit var fb_avt: ProfilePictureView
    private lateinit var normal_avt: ImageView
    private lateinit var update_avt: LinearLayout
    private lateinit var normal_avt_card: CardView
    private lateinit var fb_avt_card: CardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_ca_nhan, container, false)
        loginBtn = rootView.findViewById(R.id.Login_buton_in_person)
        username = rootView.findViewById(R.id.user_name)
        email = rootView.findViewById(R.id.service_sentence_or_email)
        logout = rootView.findViewById(R.id.log_out)
        fb_avt = rootView.findViewById(R.id.fb_avt_user)
        normal_avt = rootView.findViewById(R.id.normal_avt_user)
        update_avt = rootView.findViewById(R.id.change_image)
        normal_avt_card = rootView.findViewById(R.id.normal_avt_card)
        fb_avt_card = rootView.findViewById(R.id.fb_avt_card)

        return rootView
    }
    private val loginCode = 12345
    private val updateAvtCode = 23456

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            loginCode -> {
                if(resultCode == 1)
                    loginState()
            }
            updateAvtCode -> {}
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(UserInformation.USER == null) {
            logoutState()
        }
        else {
            loginState()
        }

    }
    private fun logoutState(){
        logoutVisibility()

        val toLogInListener = View.OnClickListener{
            var intentLogIn = Intent(requireContext(), LogInUp::class.java)
            startActivityForResult(intentLogIn, loginCode)
        }

        loginBtn.setOnClickListener(toLogInListener)
        update_avt.setOnClickListener(toLogInListener)

    }
    private fun loginState(){
        loginVisibility()

        if(UserInformation.USER!!.avatar_url != null){
            fb_avt_card.visibility = INVISIBLE
            normal_avt_card.visibility = VISIBLE
            Glide.with(this).load(UserInformation.USER!!.avatar_url).into(normal_avt)
        }else{
            fb_avt.profileId = UserInformation.USER!!.accountName
            fb_avt_card.visibility = VISIBLE
            normal_avt_card.visibility = GONE
        }
        logout.setOnClickListener {
            Firebase.auth.signOut()
            LoginManager.getInstance().logOut()

            logoutState()

            UserInformation.USER = null
            UserInformation.TOKEN = null

            val localStore = requireActivity().getSharedPreferences("vexere", Context.MODE_PRIVATE)
            val localEditor = localStore.edit()

            localEditor.putString("user",null)
            localEditor.putString("token", null)
            localEditor.commit()
        }
    }
    private fun logoutVisibility(){
        loginBtn.visibility = VISIBLE
        logout.visibility = GONE
        fb_avt_card.visibility = GONE
        normal_avt_card.visibility = GONE
        username.setText(R.string.Intro)
        email.setText(R.string.service_sentence)

    }
    private fun loginVisibility(){
        loginBtn.visibility = GONE
        logout.visibility = VISIBLE
        username.text = UserInformation.USER!!.display_name
        email.text = UserInformation.USER!!.email_contact
        update_avt.setOnClickListener(null)
    }

}