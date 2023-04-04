package com.example.kotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class BlogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog)

        findViewById<Button>(R.id.btnAddBlog).isEnabled = FBInfor.ROLE == 0 || FBInfor.ROLE == 1

        var page = 1
        val limit = 5
        var blogList = findViewById<RecyclerView>(R.id.blogList)
        val token = this.getSharedPreferences("vexere", MODE_PRIVATE).getString("token", "")
        val retrofit = APIServiceImpl()
        try {
            GlobalScope.launch(Dispatchers.IO) {
                val response =
                    retrofit.getBlog().getBlogs(page, limit).awaitResponse()
                // debug response
                Log.d("Response", response.toString())
                if (response.isSuccessful) {
                    val body = response.body()
                    launch(Dispatchers.Main) {
                        if (body != null) {
                            val blogAdapter = CustomBlogItem(body.data)

                            blogList!!.adapter = blogAdapter
                            blogList.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)

                            blogAdapter.onItemClick = {
                                val intent = Intent(this@BlogActivity, BlogDetailActivity::class.java)
                                intent.putExtra("blogId", it.id)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
    }
}