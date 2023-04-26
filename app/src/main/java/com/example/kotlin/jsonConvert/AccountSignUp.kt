package com.example.kotlin.jsonConvert

import android.content.res.Resources.getSystem
import com.example.kotlin.R
import com.google.gson.annotations.SerializedName

data class AccountSignUp(
    @SerializedName("email")
    var fbID: String,
    val password: String = "vexeREvexeRE123",
    val repassword: String = "vexeREvexeRE123",
    val display_name: String = ""
)
