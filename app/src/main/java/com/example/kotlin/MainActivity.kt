package com.example.kotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.kotlin.jsonConvert.AccountSignUp
import com.example.kotlin.jsonConvert.UserLogin
import com.facebook.*
import com.facebook.internal.CallbackManagerImpl
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class MainActivity : AppCompatActivity() {
    private lateinit var callbackManager: CallbackManager
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var db: FirebaseDatabase
    private lateinit var localEditor: SharedPreferences.Editor
    private val retrofit = APIServiceImpl()
    private var UserApi = retrofit.userService()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val localStore = getSharedPreferences("vexere", Context.MODE_PRIVATE)
        localEditor = localStore.edit()
        var token: String? = localStore.getString("token", null)


        GlobalScope.launch(Dispatchers.IO) {
            //check valid token
            var validToken: Boolean = false
            if (token != null) {

                val response = UserApi.ticketHistory("Bearer ${token}", 0, 1).awaitResponse()
                if (response.isSuccessful) {
                    validToken = true
                } else {
                    localEditor.apply {
                        putString("token", null)
                        commit()
                    }// remove token
                    launch(Dispatchers.Main) {
                        //error message here
                    }
                }
            }

            //update UI
            if (!validToken) {
                withContext(Dispatchers.Main) {
                    setContentView(R.layout.activity_main)
                    login()
                }
            } else {
                withContext(Dispatchers.Main) {
                    FBInfor.TOKEN = token
                    FBInfor.ID = localStore.getString("id", "").toString()
                    FBInfor.NAME = localStore.getString("name", "").toString()
                    FBInfor.EMAIL = localStore.getString("email", "N/A").toString()
                    FBInfor.PHOTO_URL = localStore.getString("photo", null)?.toUri()
                    FBInfor.ROLE = localStore.getInt("role", 2)
                    toHomeScreen()
                }
            }
        }
    }

    private fun login() {
        Firebase.auth.signOut()
        LoginManager.getInstance().logOut()

        FacebookSdk.sdkInitialize(applicationContext)
        callbackManager = CallbackManager.Factory.create()
        db = FirebaseDatabase.getInstance("https://kotlin-96709-default-rtdb.firebaseio.com/")
        auth = Firebase.auth

        var buttonFacebookLogin = findViewById<LoginButton>(R.id.login_button)
        buttonFacebookLogin.setReadPermissions("email", "public_profile")
        buttonFacebookLogin.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    //Log.d(TAG, "facebook:onSuccess:$loginResult")
                    buttonFacebookLogin.visibility = GONE
                    showLoadingGif()
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    //Log.d(TAG, "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    //Log.d(TAG, "facebook:onError", error)
                }
            })
    }

    fun toHomeScreen() {
        finish()
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        when (requestCode) {
            CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode() -> {
                callbackManager.onActivityResult(requestCode, resultCode, data)
            }
        }

    }
    private fun showLoadingGif(){
        var imageView = findViewById<ImageView>(R.id.login_loading)
        imageView.visibility = VISIBLE
        Glide.with(this).load(R.drawable.loading).into(imageView)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "signInWithCredential:success")
                    user = auth.currentUser!!
                    for (profile in user.providerData) {
                        if (FacebookAuthProvider.PROVIDER_ID == profile.providerId) {
                            FBInfor.ID = Profile.getCurrentProfile()?.id!!
                            profile.email?.let {
                                FBInfor.EMAIL = profile.email!!
                            }
                            profile.displayName?.let {
                                FBInfor.NAME = profile.displayName!!
                            }
                            FBInfor.PHOTO_URL = profile.photoUrl
                        }
                    }
                    db.reference.child("users")
                        .child(FBInfor.ID)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {

                                if (dataSnapshot.exists()) {
                                    db.reference.child("users").child(FBInfor.ID).apply {
                                        child("Name").setValue(FBInfor.NAME)
                                    }//update name on firebase database

                                    GlobalScope.launch(Dispatchers.IO) {
                                        val response =
                                            UserApi.signIn(UserLogin(FBInfor.ID)).awaitResponse()
                                        if (response.isSuccessful) {
                                            val body = response.body()
                                            body?.let {
                                                FBInfor.ROLE = body.user.role
                                                FBInfor.TOKEN = body.token.token
                                                this@MainActivity.localEditor.apply {
                                                    putString("token", body.token.token)
                                                    putString("id", FBInfor.ID)
                                                    putString("name", FBInfor.NAME)
                                                    putString("email", FBInfor.EMAIL)
                                                    putString("photo", FBInfor.PHOTO_URL.toString())
                                                    putInt("role", FBInfor.ROLE)
                                                    commit()
                                                }
                                            }
                                            //Log.d("Response", body.toString())
                                            withContext(Dispatchers.Main){
                                                toHomeScreen()
                                            }

                                        } else {
                                            launch(Dispatchers.Main) {
                                                //error message here
                                            }
                                        }
                                    }


//                                auth.signOut()
//                                LoginManager.getInstance().logOut()
                                    //log out firebase and facebook code

                                } else {
                                    // User does not exist. NOW call createUserWithEmailAndPassword

                                    db.reference.child("users").child(FBInfor.ID).apply {
                                        child("Name").setValue(FBInfor.NAME)
                                    }//create name on firebase database
                                    GlobalScope.launch(Dispatchers.IO) {

                                        val response = UserApi.signUp(AccountSignUp(FBInfor.ID))
                                            .awaitResponse()
                                        if (response.isSuccessful) {
                                            val body = response.body()
                                            body?.let {
                                                FBInfor.TOKEN = body.token.token
                                                this@MainActivity.localEditor.apply {
                                                    putString("token", body.token.token)
                                                    putString("id", FBInfor.ID)
                                                    putString("name", FBInfor.NAME)
                                                    putString("email", FBInfor.EMAIL)
                                                    putString("photo", FBInfor.PHOTO_URL.toString())
                                                    putInt("role", FBInfor.ROLE)
                                                    commit()
                                                }
                                                withContext(Dispatchers.Main){
                                                    toHomeScreen()
                                                }
                                            }
                                            //Log.d("Response", body.toString())
                                        } else {
                                            launch(Dispatchers.Main) {
                                                //error message here
                                            }
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                print(databaseError.message)
                            }
                        })


//                    var intent = Intent(this, MainActivity2::class.java)
//                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


}