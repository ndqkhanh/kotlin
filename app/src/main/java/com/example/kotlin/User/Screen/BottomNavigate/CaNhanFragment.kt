package com.example.kotlin.User.Screen.BottomNavigate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.kotlin.APIServiceImpl
import com.example.kotlin.DataClass.SuccessMessage
import com.example.kotlin.UserAdmin.Screen.LogInUp
import com.example.kotlin.R
import com.example.kotlin.utils.UploadFile
import com.example.kotlin.utils.UserInformation
import com.example.kotlin.utils.WaitingAsyncClass
import com.facebook.login.LoginManager
import com.facebook.login.widget.ProfilePictureView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        loginBtn = rootView.findViewById(R.id.loginBtn)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(UserInformation.USER == null) {
            logoutState()
        }
        else {
            loginState()
        }

    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if(UserInformation.USER == null) {
            logoutState()
        }
        else {
            loginState()
        }
    }

    override fun onResume() {
        super.onResume()
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
            startActivity(intentLogIn)
        }

        loginBtn.setOnClickListener(toLogInListener)
        update_avt.setOnClickListener(null)
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
        update_avt.setOnClickListener{
            selectImage()
        }
        logout.setOnClickListener {
            Firebase.auth.signOut()
            LoginManager.getInstance().logOut()

            logoutState()
            val role = UserInformation.USER!!.role
            UserInformation.USER = null
            UserInformation.TOKEN = null

            val localStore = requireActivity().getSharedPreferences("vexere", Context.MODE_PRIVATE)
            val localEditor = localStore.edit()

            localEditor.putString("user",null)
            localEditor.putString("token", null)
            localEditor.commit()

            if(role != 2){
                requireActivity().finishAffinity()
                var userBottom = Intent(requireContext(), BottomNavigation::class.java)
                startActivity(userBottom)
            }

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

    }

    private fun selectImage(){
        // select image from local storage
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100){
            // get image from local storage
            val imageUri = data?.data
            if(imageUri != null) {
                GlobalScope.launch(Dispatchers.IO) {
                    val fileUpload = UploadFile()
                    val downloadUri = fileUpload.uploadImageToFirebase(imageUri!!).await()
                    val callUpdate = APIServiceImpl
                        .userService()
                        .updateAvatar("Bearer ${UserInformation.TOKEN}", SuccessMessage(avatar_url = downloadUri.toString()))
                    val res: SuccessMessage? = WaitingAsyncClass(callUpdate).execute().get()
                    if(res != null){
                        UserInformation.USER!!.avatar_url = downloadUri.toString()
                        launch(Dispatchers.Main) {
                            if(UserInformation.USER!!.avatar_url != null){
                                fb_avt_card.visibility = INVISIBLE
                                normal_avt_card.visibility = VISIBLE
                                Glide.with(this@CaNhanFragment).load(UserInformation.USER!!.avatar_url).into(normal_avt)
                            }else{
                                fb_avt.profileId = UserInformation.USER!!.accountName
                                fb_avt_card.visibility = VISIBLE
                                normal_avt_card.visibility = GONE
                            }
                        }
                    }
                }
            }
        }
    }
}