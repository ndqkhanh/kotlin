package com.example.kotlin

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlin.jsonConvert.*
import com.facebook.login.widget.LoginButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.internal.wait
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
    private val UserAPI = APIServiceImpl().userService()
    private lateinit var localEditor: SharedPreferences.Editor
    private val gson = Gson()

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
        back = findViewById(R.id.back_from_log_in)
        logInSuggest = findViewById(R.id.goi_y_dang_nhap)
        logUpSuggest = findViewById(R.id.goi_y_dang_ky)
        switchToLogIn = findViewById(R.id.change_to_dang_nhap)
        switchToLogUp = findViewById(R.id.change_to_dang_ky)
        redNotice = findViewById(R.id.red_note)
        buttonFacebookLogin = findViewById(R.id.fb_login_button)

        val localStore = getSharedPreferences("vexere", Context.MODE_PRIVATE)
        localEditor = localStore.edit()

        switchToLogIn.setOnClickListener { startLogin() }
        switchToLogUp.setOnClickListener { startLogup() }
        back.setOnClickListener { backToPrevious() }
        dangKy.setOnClickListener { baseApplogUp() }
        dangNhap.setOnClickListener { bassAppLogIn() }


    }
    private fun bassAppLogIn(){
        var email_str = email.text.toString()
        var pass_str = password.text.toString()

        var callLogIn: Call<UserLogInRespone> = UserAPI.signIn(UserLogin(email_str,pass_str))
        var respone: UserLogInRespone? = WaitingAsyncClass(callLogIn).execute().get()
        if(respone != null){
            storeLocally(respone.user, respone.token.token)
            finish()
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
            storeLocally(newAccount.user, newAccount.token.token)
            finish()
        }else{
            doRedNote(failure)
            return
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
