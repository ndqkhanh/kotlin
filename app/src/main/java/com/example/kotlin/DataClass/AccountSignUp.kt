package com.example.kotlin.DataClass

import com.google.gson.annotations.SerializedName

data class AccountSignUp(
    @SerializedName("email")
    var username: String,
    val password: String = "vexeREvexeRE123",
    val repassword: String = "vexeREvexeRE123",
    val display_name: String = ""
)
