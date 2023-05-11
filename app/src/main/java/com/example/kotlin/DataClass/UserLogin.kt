package com.example.kotlin.DataClass

import com.google.gson.annotations.SerializedName

data class UserLogin(
    @SerializedName("email")
    val username: String,
    val password: String = "vexeREvexeRE123"
)