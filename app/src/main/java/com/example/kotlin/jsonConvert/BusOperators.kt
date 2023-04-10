package com.example.kotlin.jsonConvert

import kotlinx.serialization.Serializable

@Serializable
data class BusOperators(
    val id: String,
    val image_url: String,
    val name: String,
    val phone: String
)