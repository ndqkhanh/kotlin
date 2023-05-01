package com.example.kotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlin.User.Screen.BottomNavigate.BottomNavigation
import com.example.kotlin.jsonConvert.HistoryList
import com.example.kotlin.jsonConvert.User
import com.facebook.CallbackManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import java.lang.reflect.Type

class MainActivity : AppCompatActivity() {
    private lateinit var callbackManager: CallbackManager
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var db: FirebaseDatabase
    private lateinit var localEditor: SharedPreferences.Editor
    private val retrofit = APIServiceImpl()
    private val UserAPI = APIServiceImpl().userService()

    private fun getLocalData(){
        var gson = Gson()
        val localStore = getSharedPreferences("vexere", Context.MODE_PRIVATE)
        var str_json_user = localStore.getString("user", null)
        var token = localStore.getString("token", null)

        token?.let {
            var callLogIn: Call<HistoryList> = UserAPI.ticketHistory("Bearer ${token!!}",0,1)
            var respone: HistoryList? = WaitingAsyncClass(callLogIn).execute().get()

            //token còn dùng được
            if(respone != null) {
                val userType: Type = object : TypeToken<User?>() {}.type
                UserInformation.USER = gson.fromJson(str_json_user, userType)
                UserInformation.TOKEN = token
                Log.i("!23", UserInformation.USER!!.display_name!!)
            }else{
                UserInformation.USER = null
                UserInformation.TOKEN = null
            }
        }
        if(token == null){
            UserInformation.USER = null
            UserInformation.TOKEN = null
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseMessaging.getInstance().subscribeToTopic("FCM")
            .addOnCompleteListener { task ->
                var msg = "Done"
                if (!task.isSuccessful) {
                    msg = "Failed"
                }
                Log.d("Received FCM", msg)
            }



        getLocalData()

        val userIntent = Intent(this, BottomNavigation::class.java)
        val adminIntent = Intent(this, AdminActivity::class.java)
        Log.d("UserInformation", UserInformation.USER?.accountName.toString())

        if(UserInformation.USER != null){
            if(UserInformation.USER?.role == 2){
//                Log.d("!@#","2")
                finish()
                startActivity(userIntent)
            }else{
//                Log.d("!@#","3")
                finish()
                // TODO qua admin ở đây
                startActivity(adminIntent)
            }
        }else{
//            Log.d("!@#","4")
            finish()
            startActivity(userIntent)
        }






//    }
//
//    private fun login() {
//        Firebase.auth.signOut()
//        LoginManager.getInstance().logOut()
//
//        FacebookSdk.sdkInitialize(applicationContext)
//        callbackManager = CallbackManager.Factory.create()
//        db = FirebaseDatabase.getInstance("https://kotlin-96709-default-rtdb.firebaseio.com/")
//        auth = Firebase.auth
//
//        var buttonFacebookLogin = findViewById<LoginButton>(R.id.login_button)
//        buttonFacebookLogin.setReadPermissions("email", "public_profile")
//        buttonFacebookLogin.registerCallback(
//            callbackManager,
//            object : FacebookCallback<LoginResult> {
//                override fun onSuccess(loginResult: LoginResult) {
//                    //Log.d(TAG, "facebook:onSuccess:$loginResult")
//                    buttonFacebookLogin.visibility = GONE
//                    showLoadingGif()
//                    handleFacebookAccessToken(loginResult.accessToken)
//                }
//
//                override fun onCancel() {
//                    //Log.d(TAG, "facebook:onCancel")
//                }
//
//                override fun onError(error: FacebookException) {
//                    //Log.d(TAG, "facebook:onError", error)
//                }
//            })
//    }
//
//    fun toHomeScreen() {
//        finish()
//        val intent = Intent(this, Home::class.java)
//        startActivity(intent)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        // Pass the activity result back to the Facebook SDK
//        when (requestCode) {
//            CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode() -> {
//                callbackManager.onActivityResult(requestCode, resultCode, data)
//            }
//        }
//
//    }
//    private fun showLoadingGif(){
//        var imageView = findViewById<ImageView>(R.id.login_loading)
//        imageView.visibility = VISIBLE
//        Glide.with(this).load(R.drawable.loading).into(imageView)
//    }
//
//    private fun handleFacebookAccessToken(token: AccessToken) {
//
//        val credential = FacebookAuthProvider.getCredential(token.token)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    //Log.d(TAG, "signInWithCredential:success")
//                    user = auth.currentUser!!
//                    for (profile in user.providerData) {
//                        if (FacebookAuthProvider.PROVIDER_ID == profile.providerId) {
//                            FBInfor.ID = Profile.getCurrentProfile()?.id!!
//                            profile.email?.let {
//                                FBInfor.EMAIL = profile.email!!
//                            }
//                            profile.displayName?.let {
//                                FBInfor.NAME = profile.displayName!!
//                            }
//                            FBInfor.PHOTO_URL = profile.photoUrl
//                        }
//                    }
//                    db.reference.child("users")
//                        .child(FBInfor.ID)
//                        .addListenerForSingleValueEvent(object : ValueEventListener {
//                            override fun onDataChange(dataSnapshot: DataSnapshot) {
//
//                                if (dataSnapshot.exists()) {
//                                    db.reference.child("users").child(FBInfor.ID).apply {
//                                        child("Name").setValue(FBInfor.NAME)
//                                    }//update name on firebase database
//
//                                    GlobalScope.launch(Dispatchers.IO) {
//                                        val response =
//                                            UserApi.signIn(UserLogin(FBInfor.ID)).awaitResponse()
//                                        if (response.isSuccessful) {
//                                            val body = response.body()
//                                            body?.let {
//                                                FBInfor.ROLE = body.user.role
//                                                FBInfor.TOKEN = body.token.token
//                                                this@MainActivity.localEditor.apply {
//                                                    putString("token", body.token.token)
//                                                    putString("id", FBInfor.ID)
//                                                    putString("name", FBInfor.NAME)
//                                                    putString("email", FBInfor.EMAIL)
//                                                    putString("photo", FBInfor.PHOTO_URL.toString())
//                                                    putInt("role", FBInfor.ROLE)
//                                                    commit()
//                                                }
//                                            }
//                                            //Log.d("Response", body.toString())
//                                            withContext(Dispatchers.Main){
//                                                toHomeScreen()
//                                            }
//
//                                        } else {
//                                            launch(Dispatchers.Main) {
//                                                //error message here
//                                            }
//                                        }
//                                    }
//
//
////                                auth.signOut()
////                                LoginManager.getInstance().logOut()
//                                    //log out firebase and facebook code
//
//                                } else {
//                                    // User does not exist. NOW call createUserWithEmailAndPassword
//
//                                    db.reference.child("users").child(FBInfor.ID).apply {
//                                        child("Name").setValue(FBInfor.NAME)
//                                    }//create name on firebase database
//                                    GlobalScope.launch(Dispatchers.IO) {
//
//                                        val response = UserApi.signUp(AccountSignUp(FBInfor.ID))
//                                            .awaitResponse()
//                                        if (response.isSuccessful) {
//                                            val body = response.body()
//                                            body?.let {
//                                                FBInfor.TOKEN = body.token.token
//                                                this@MainActivity.localEditor.apply {
//                                                    putString("token", body.token.token)
//                                                    putString("id", FBInfor.ID)
//                                                    putString("name", FBInfor.NAME)
//                                                    putString("email", FBInfor.EMAIL)
//                                                    putString("photo", FBInfor.PHOTO_URL.toString())
//                                                    putInt("role", FBInfor.ROLE)
//                                                    commit()
//                                                }
//                                                withContext(Dispatchers.Main){
//                                                    toHomeScreen()
//                                                }
//                                            }
//                                            //Log.d("Response", body.toString())
//                                        } else {
//                                            launch(Dispatchers.Main) {
//                                                //error message here
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//
//                            override fun onCancelled(databaseError: DatabaseError) {
//                                print(databaseError.message)
//                            }
//                        })
//
//
////                    var intent = Intent(this, MainActivity2::class.java)
////                    startActivity(intent)
//
//                } else {
//                    // If sign in fails, display a message to the user.
//                    //Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    Toast.makeText(
//                        this, "Authentication failed.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
    }


}