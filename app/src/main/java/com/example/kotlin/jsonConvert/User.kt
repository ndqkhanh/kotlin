package com.example.kotlin.jsonConvert

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val avatar_url: String?,
    val boid: String?,
    val display_name: String?,
    @SerializedName("email")
    val accountName: String,
    val id: String? = null,
    val role: Int,
    val verification: Boolean
)