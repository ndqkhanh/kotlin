package com.example.kotlin.DataClass

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    var avatar_url: String?,
    val boid: String?,
    var display_name: String?,
    @SerializedName("email")
    val accountName: String,
    val id: String? = null,
    val role: Int,
    var email_contact: String? = null, //email của user lấy ở đây
    val verification: Boolean
)