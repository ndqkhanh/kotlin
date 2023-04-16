package com.example.kotlin

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class AdminBlogCreateActivity : AppCompatActivity() {
    private lateinit var localEditor: SharedPreferences.Editor
    private lateinit var txtThumbnail: EditText
    private lateinit var txtTitle: EditText
    private lateinit var txtContent: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnLogOut: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_blog_create)

        txtThumbnail = findViewById(R.id.txtThumbnail)
        txtTitle = findViewById(R.id.txtTitle)
        txtContent = findViewById(R.id.txtContent)
        btnAdd = findViewById(R.id.btnAdd)
        btnLogOut = findViewById(R.id.btnLogOut)

        val localStore = getSharedPreferences("vexere", Context.MODE_PRIVATE)
        localEditor = localStore.edit()
        btnLogOut.setOnClickListener {
            Log.d("Response", "Đăng xuất")
            localEditor.apply {
                putString("token", null)
                commit()
            }// remove token

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        val token = this.getSharedPreferences("vexere", MODE_PRIVATE)
            .getString("token", "")

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
                                retrofit.manipulateBlog(token!!)
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