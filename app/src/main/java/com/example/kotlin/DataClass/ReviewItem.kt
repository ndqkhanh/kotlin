package com.example.kotlin.DataClass

data class ReviewItem(
    val account_name: String,
    val avatar_url: String?,
    val comment: String,
    val name: String,
    val rate: Int,
    val ngay_review: String
)