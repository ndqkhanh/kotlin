package com.example.kotlin

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.gms.tasks.Task

class UploadFile {
    private var imageURL = ""

    fun uploadImageToFirebase(imageUri: Uri): Task<Uri> {
        val firebaseStorage = FirebaseStorage.getInstance()
        val formater = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault())
        val now = Date()
        val fileName = formater.format(now)
        val storageRef = firebaseStorage.reference
        val imageRef = storageRef.child("images/$fileName")
        val uploadTask = imageRef.putFile(imageUri)
        return uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception!!
            }
            // get image url
            imageRef.downloadUrl
        }
    }

    fun getImageURL(): String{
        return imageURL
    }
}