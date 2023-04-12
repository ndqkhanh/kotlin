package com.example.kotlin

import android.net.Uri
import kotlinx.coroutines.Job

class FBInfor {
    companion object{
        var TOKEN: String? = null
        var ID: String = ""
        var NAME: String = ""
        var EMAIL: String = "N/A"
        var ROLE: Int = 2
        var PHOTO_URL: Uri? = null
    }
}