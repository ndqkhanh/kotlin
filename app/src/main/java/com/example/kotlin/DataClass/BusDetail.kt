package com.example.kotlin.DataClass

data class BusDetail(
    val anh_nha_xe: String,
    val anh_xe: String,
    val end_date: String,
    val end_time: String,
    val policy: String,
    val sdt_nha_xe: String,
    val start_date: String,
    val start_time: String,
    val ten_nha_xe: String,
    val ds_don: List<Point>,
    val ds_tra: List<Point>,
)