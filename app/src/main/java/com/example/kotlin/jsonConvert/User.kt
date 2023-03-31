package com.example.kotlin.jsonConvert

import com.google.gson.annotations.SerializedName

data class User(
    val avatar_url: String?,
    val boid: String?,
    val create_time: String,
    val display_name: String?,
    @SerializedName("email")
    val fbID: String,
    val id: String,
    val role: Int,
    val update_time: String,
    val verification: Boolean
)