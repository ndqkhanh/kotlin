package com.example.kotlin

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse


class BlogDetailActivity : AppCompatActivity() {
    private lateinit var imgBlogDetail: ImageView
    private lateinit var txtTitle: TextView
    private lateinit var txtContent: TextView
    private lateinit var txtCreateTime: TextView
    private lateinit var btnDelete: Button
    private lateinit var btnBack: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_detail)

        imgBlogDetail = findViewById(R.id.imgBlogDetail)
        txtTitle = findViewById(R.id.txtTitle)
        txtContent = findViewById(R.id.txtContent)
        txtCreateTime = findViewById(R.id.txtCreateTime)
        btnDelete = findViewById(R.id.btnDelete)
        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val blogId = intent.getStringExtra("blogId")
        val token = this.getSharedPreferences("vexere", MODE_PRIVATE)
            .getString("token", "")
        val retrofit = APIServiceImpl()
        try {
            GlobalScope.launch(Dispatchers.IO) {
                val response =
                    retrofit.getBlog().getBlogById(blogId!!).awaitResponse()
                // debug response
                Log.d("Response", response.toString())
                if (response.isSuccessful) {
                    Log.d("Response", response.body().toString())
                    launch(Dispatchers.Main) {
                        val blog = response.body()
                        txtTitle.text = blog?.title
                        txtContent.text = blog?.content
                        txtCreateTime.text = blog?.created_time
                        Glide.with(imgBlogDetail.context)
                            .load(blog?.thumbnail)
                            .into(imgBlogDetail)
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }

        btnDelete.setOnClickListener {
            try {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Delete Blog")
                dialog.setMessage("Are you sure you want to delete this blog?")
                dialog.setPositiveButton("Yes") { _, _ ->
                    GlobalScope.launch(Dispatchers.IO) {
                        val response =
                            retrofit.manipulateBlog(token!!).deleteBlogById(blogId!!)
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