package com.example.kotlin.jsonConvert

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val avatar_url: String?,
    val boid: String?,
    var display_name: String?,
    @SerializedName("email")
    val accountName: String,
    val id: String? = null,
    val role: Int,
    var email_contact: String? = null,
    val verification: Boolean
)