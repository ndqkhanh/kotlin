package com.example.kotlin.jsonConvert

import java.io.Serializable


data class History(
    val bus_id: String,
    val dia_chi_diem_don: String,
    val dia_chi_diem_tra: String,
    val end_date: String,
    val end_time: String,
    val id: String,
    val note: String?,
    val phone: String,
    val price: Int,
    val seats: String,
    val start_date: String,
    val start_time: String,
    val status: Int,
    val ten_diem_don: String,
    val ten_diem_tra: String,
    val ten_nha_xe: String,
    val tinh_don: String,
    val tinh_tra: String,
    val type: Int,
): Serializable