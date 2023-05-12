package com.example.kotlin.DataClass

data class ReviewItem(
    val account_name: String? = null,
    val avatar_url: String? = null,
    val comment: String,
    val name: String? = null,
    val rate: Int,
    val ngay_review: String? = null
)