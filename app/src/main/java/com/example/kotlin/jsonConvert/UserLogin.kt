package com.example.kotlin.jsonConvert

import android.content.res.Resources
import com.example.kotlin.R
import com.google.gson.annotations.SerializedName

data class UserLogin(
    @SerializedName("email")
    val username: String,
    val password: String = "vexeREvexeRE123"
)