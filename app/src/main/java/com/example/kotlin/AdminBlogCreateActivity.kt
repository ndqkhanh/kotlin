package com.example.kotlin

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class AdminBlogCreateActivity : AppCompatActivity() {
    private lateinit var txtThumbnail: EditText
    private lateinit var txtTitle: EditText
    private lateinit var txtContent: EditText
    private lateinit var btnAdd: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_blog_create)

        txtThumbnail = findViewById(R.id.txtThumbnail)
        txtTitle = findViewById(R.id.txtTitle)
        txtContent = findViewById(R.id.txtContent)
        btnAdd = findViewById(R.id.btnAdd)

        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJjMTE4ZjY5My04NzIyLTQ0NjEtYTc5ZC1kNzY5OTFiOTZiY2QiLCJpYXQiOjE2ODAxNDM2NDYsImV4cCI6MTY4MDE0NTQ0NiwidHlwZSI6ImFjY2VzcyJ9.UvJAYdpGmrOyIN6SB-3t0LdcS4ySpT5cO7vON7qH5KU"

        btnAdd.setOnClickListener {
            val thumbnail = txtThumbnail.text.toString()
            val title = txtTitle.text.toString()
            val content = txtContent.text.toString()
            var flag = false
            if (thumbnail.isEmpty()) {
                txtThumbnail.error = "Please enter thumbnail"
                flag = true
            }
            if (title.isEmpty()) {
                txtTitle.error = "Please enter title"
                flag = true
            }
            if (content.isEmpty()) {
                txtContent.error = "Please enter content"
                flag = true
            }
            if (!flag) {
                val retrofit = APIServiceImpl()
                try {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Add blog")
                    dialog.setMessage("Are you sure you want to add this blog?")
                    dialog.setPositiveButton("Yes") { _, _ ->
                        GlobalScope.launch(Dispatchers.IO) {
                            val response =
                                retrofit.manipulateBlog(token)
                                    .createBlog(BlogData(thumbnail, title, content))
                                    .awaitResponse()
                            // debug response
                            Log.d("Response", response.toString())
                            if (response.isSuccessful) {
                                Log.d("Response", response.body().toString())
                                launch(Dispatchers.Main) {
                                    finish()
                                }
                            }
                        }
                    }
                    dialog.setNegativeButton("No") { dialogInterface: DialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    dialog.show()
                } catch (e: Exception) {
                    Log.d("Error", e.toString())
                }
            }
        }
    }
}