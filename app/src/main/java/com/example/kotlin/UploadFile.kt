package com.example.kotlin

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UploadFile {
    private var imageURL = ""

    fun uploadImageToFirebase(imageUri: Uri){
        // upload image to firebase
        val firebaseStorage = FirebaseStorage.getInstance()
        val formater = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault())
        val now = Date()
        val fileName = formater.format(now)
        val storageRef = firebaseStorage.reference
        val imageRef = storageRef.child("images/$fileName")
        val uploadTask = imageRef.putFile(imageUri).addOnSuccessListener {
            // get image url
            imageRef.downloadUrl.addOnSuccessListener {
                imageURL = it.toString()
                Log.d("imageURL", imageURL)
            }
        } .addOnFailureListener{
            // Handle unsuccessful uploads. alert dialog
            Log.d("uploadImage", "upload image failed")
        }
    }

    fun getImageURL(): String{
        return imageURL
    }
}