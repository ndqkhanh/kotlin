package com.example.kotlin

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.facebook.login.widget.LoginButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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

    private var invaliteEmailFormat = "Không đúng định dạng email !!"

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

        switchToLogIn.setOnClickListener { startLogin() }
        switchToLogUp.setOnClickListener { startLogup() }
        back.setOnClickListener { backToPrevious() }
        dangKy.setOnClickListener { baseApplogUp() }


    }
    private fun baseApplogUp(){
        var email_str = email.text.toString()
        if(!emailFormat(email_str)){
            redNotice.visibility = VISIBLE
            redNotice.text = invaliteEmailFormat
            return
        }

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

    private fun emailFormat(str: String?): Boolean{
        if (str == null) return false
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()

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
}