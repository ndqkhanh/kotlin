package com.example.kotlin

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlin.Admin.Screen.BottomNavigation.BottomAdminNavigation
import com.example.kotlin.jsonConvert.*
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import retrofit2.Call
import java.util.regex.Pattern

class LogInUp: AppCompatActivity() {

    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var repassword: TextInputEditText
    private lateinit var repassword_layout: TextInputLayout
    private lateinit var name: TextInputEditText
    private lateinit var enter_name_layout: TextInputLayout
    private lateinit var dangKy: Button
    private lateinit var dangNhap: Button
    private lateinit var back: ImageButton
    private lateinit var logInSuggest: LinearLayout
    private lateinit var logUpSuggest: LinearLayout
    private lateinit var switchToLogIn: TextView
    private lateinit var switchToLogUp: TextView
    private lateinit var redNotice: TextView
    private lateinit var buttonFacebookLogin: LoginButton
    private val UserAPI = APIServiceImpl.userService()
    private lateinit var localEditor: SharedPreferences.Editor
    private val gson = Gson()
    private lateinit var auth: FirebaseAuth

    private val invaliteEmailFormat = "Không đúng định dạng email !!"
    private val notMatchPassword = "Mật khẩu nhập lại không khớp."
    private val userExisted = "Người dùng đã tồn tại"
    private val failure = "Yêu cầu thất bại"
    private val passNotEnoughChar = "Mật khẩu cần ít nhất 1 số, 1 chữ cái và ít nhất 8 ký tự"
    private val blankName = "Tên không được để trống"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dang_nhap)

        email = findViewById(R.id.enter_email)
        password = findViewById(R.id.enter_password)
        repassword = findViewById(R.id.re_enter_password)
        repassword_layout = findViewById(R.id.re_enter_password_layout)
        name = findViewById(R.id.enter_name)
        enter_name_layout = findViewById(R.id.enter_name_layout)
        dangKy = findViewById(R.id.dang_ky)
        dangNhap = findViewById(R.id.dang_nhap)
        back = findViewById(R.id.back_from_login)
        logInSuggest = findViewById(R.id.goi_y_dang_nhap)
        logUpSuggest = findViewById(R.id.goi_y_dang_ky)
        switchToLogIn = findViewById(R.id.change_to_dang_nhap)
        switchToLogUp = findViewById(R.id.change_to_dang_ky)
        redNotice = findViewById(R.id.red_note)
        buttonFacebookLogin = findViewById(R.id.fb_login_button)

        val localStore = getSharedPreferences("vexere", Context.MODE_PRIVATE)
        localEditor = localStore.edit()
        val dialog = ProgressDialog(this)
        dialog.setCancelable(false)


        switchToLogIn.setOnClickListener { startLogin() }
        switchToLogUp.setOnClickListener { startLogup() }
        back.setOnClickListener { backToPrevious() }
        dangKy.setOnClickListener {
            dialog.show()
            baseApplogUp()
            dialog.dismiss()
        }
        dangNhap.setOnClickListener {
            dialog.show()
            bassAppLogIn()
            dialog.dismiss()
        }

        loginByFacebook()

    }
    private fun loginByFacebook(){
        auth = Firebase.auth
        FacebookSdk.sdkInitialize(applicationContext)
        var callbackManager = CallbackManager.Factory.create()
        buttonFacebookLogin.setReadPermissions("email", "public_profile")
        buttonFacebookLogin.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    handleFacebookAccessTokenAndLogIn(loginResult.accessToken)

                }

                override fun onCancel() {
                    //Log.d(TAG, "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    //Log.d(TAG, "facebook:onError", error)
                }
            })
    }
    private fun handleFacebookAccessTokenAndLogIn(token: AccessToken) {
        val dialog = ProgressDialog(this)
        dialog.setCancelable(false)
        dialog.show()
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "signInWithCredential:success")
                    var user: FirebaseUser = auth.currentUser!!
                    var id: String = ""
                    var email: String = ""
                    var name: String = ""
                    for (profile in user.providerData) {
                        if (FacebookAuthProvider.PROVIDER_ID == profile.providerId) {
                            id = Profile.getCurrentProfile()?.id!!
                            profile.email?.let {
                                email = profile.email!!
                            }
                            profile.displayName?.let {
                                name = profile.displayName!!
                            }
                        }
                    }
                    var callUser: Call<User> = UserAPI.getUserByAccountName(id)
                    var systemUser: User? = WaitingAsyncClass(callUser).execute().get()
                    if(systemUser != null){

                        var callLogIn: Call<UserLogInRespone> = UserAPI.signIn(UserLogin(username = id))
                        var respone: UserLogInRespone? = WaitingAsyncClass(callLogIn).execute().get()
                        if(respone != null){
                            respone.user.email_contact = email
                            respone.user.display_name = name
                            storeLocally(respone.user, respone.token.token)
                            navigateBaseOnRole(respone.user.role)
                        }else{
                            doRedNote(failure)
                        }

                    }else{
                        var newUser = AccountSignUp(username = id ,display_name = name )
                        var callSignUp: Call<UserSignUpRespone> = UserAPI.signUp(newUser)
                        var newAccount: UserSignUpRespone? = WaitingAsyncClass(callSignUp).execute().get()
                        if(newAccount != null){
                            newAccount.user.email_contact = email
                            newAccount.user.display_name = name
                            storeLocally(newAccount.user, newAccount.token.token)
                            navigateBaseOnRole(newAccount.user.role)
                        }else{
                            doRedNote(failure)
                        }
                    }
                }else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            dialog.dismiss()
            }

    }
    private fun bassAppLogIn(){
        var email_str = email.text.toString()
        var pass_str = password.text.toString()

        var callLogIn: Call<UserLogInRespone> = UserAPI.signIn(UserLogin(email_str,pass_str))
        var respone: UserLogInRespone? = WaitingAsyncClass(callLogIn).execute().get()
        if(respone != null){
            respone.user.email_contact = respone.user.accountName
            storeLocally(respone.user, respone.token.token)
            navigateBaseOnRole(respone.user.role)
        }else{
            doRedNote(failure)
            return
        }
    }
    private fun baseApplogUp(){

        var email_str = email.text.toString()
        var pass_str = password.text.toString()
        var repass_str = repassword.text.toString()
        var name_str = name.text.toString()

        redNotice.visibility = GONE

        if(!emailFormat(email_str)){
            doRedNote(invaliteEmailFormat)
            return
        }
        if(pass_str != repass_str){
            doRedNote(notMatchPassword)
            return
        }
        if(!passwordFormate(pass_str)) {
            doRedNote(passNotEnoughChar)
            return
        }
        if(name_str.isBlank()){
            doRedNote(blankName)
            return
        }

        var callUser: Call<User> = UserAPI.getUserByAccountName(email_str)
        var user: User? = WaitingAsyncClass(callUser).execute().get()
        if(user != null){
            doRedNote(userExisted)
            return
        }

        var newUser = AccountSignUp(email_str ,pass_str ,repass_str ,name_str )
        var callSignUp: Call<UserSignUpRespone> = UserAPI.signUp(newUser)
        var newAccount: UserSignUpRespone? = WaitingAsyncClass(callSignUp).execute().get()
        if(newAccount != null){
            newAccount.user.email_contact = newAccount.user.accountName
            storeLocally(newAccount.user, newAccount.token.token)
            navigateBaseOnRole(newAccount.user.role)
        }else{
            doRedNote(failure)
            return
        }


    }
    private fun navigateBaseOnRole(role: Int?){
        if (role != null){
            if(role == 2) {
                finish()
            }
            else{
                finishAffinity()
                val adminIntent = Intent(this, BottomAdminNavigation::class.java)
                startActivity(adminIntent)
            }
        }else{
            finish()
        }
    }
    private fun doRedNote(str: String){
        redNotice.visibility = VISIBLE
        redNotice.text = str
    }
    private fun backToPrevious(){
        finish()
    }
    private fun startLogin(){
        //appear
        dangNhap.visibility = VISIBLE
        logUpSuggest.visibility = VISIBLE

        //disappear
        dangKy.visibility = GONE
        repassword_layout.visibility = GONE
        enter_name_layout.visibility = GONE
        logInSuggest.visibility = GONE


    }
    private fun startLogup(){
        //appear
        repassword_layout.visibility = VISIBLE
        enter_name_layout.visibility = VISIBLE
        dangKy.visibility = VISIBLE
        logInSuggest.visibility = VISIBLE

        //disappear
        dangNhap.visibility = GONE
        logUpSuggest.visibility = GONE

    }
    private fun storeLocally(user: User?, token: String?){
        UserInformation.USER = user
        UserInformation.TOKEN = token
        localEditor.putString("user",gson.toJson(UserInformation.USER))
        localEditor.putString("token", UserInformation.TOKEN)
        localEditor.commit()
    }

    private fun emailFormat(str: String?): Boolean{
        if (str == null) return false
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()

    }
    private fun passwordFormate(str: String?): Boolean{
        if(str == null) return false
        return PASSWORD_PATTERN.matcher(str).matches()
    }

    private val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    private val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^"+
                "(?=.*[0-9])" + //at least 1 number
                "(?=.*[a-zA-Z])" + //at least 1 letter
                "(.{8,})" +  //at least 8 character
                "$"
    )
}
