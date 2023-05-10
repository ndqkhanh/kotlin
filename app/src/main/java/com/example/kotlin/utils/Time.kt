package com.example.kotlin.utils

import java.text.SimpleDateFormat

// convert date string into format dd MMM yyy, hh:mm
fun dateFormat(date: String): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val date = dateFormat.parse(date)

    val timeFormat = SimpleDateFormat("hh:mm")
    val dateFormat2 = SimpleDateFormat("dd MMM yyyy")
    val formattedTime = timeFormat.format(date)
    val formattedDate = dateFormat2.format(date)
    val result = "$formattedTime, $formattedDate"

    return result
}

// distance between 2 date string and format into hh:mm
fun getDuration(startTime: String, endTime: String): String{
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val date1 = dateFormat.parse(startTime)
    val date2 = dateFormat.parse(endTime)

    val timeDifference = date2.time - date1.time

    val minutes = (timeDifference / 1000 / 60) % 60
    val hours = (timeDifference / 1000 / 60 / 60)

    val result = String.format("%02d h %02d m", hours, minutes)

    return result
}