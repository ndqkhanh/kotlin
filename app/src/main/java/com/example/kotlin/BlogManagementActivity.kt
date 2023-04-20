package com.example.kotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class BlogManagementActivity : AppCompatActivity() {
    private lateinit var blogList: RecyclerView
    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button
    private lateinit var btnBack: ImageView
    val retrofit = APIServiceImpl()
    private var page = 1
    private val limit = 5
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_management)

        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        blogList = findViewById(R.id.blogList)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)
        val btnAddBlog = findViewById<Button>(R.id.btnAddBlog)
        btnAddBlog.visibility = if (FBInfor.ROLE == 0 || FBInfor.ROLE == 1) {
            View.VISIBLE
        } else {
            View.GONE
        }
        btnAddBlog.setOnClickListener {
            if (FBInfor.ROLE == 0 || FBInfor.ROLE == 1) {
                val intent = Intent(this, AdminBlogCreateActivity::class.java)
                startActivity(intent)
            }
        }
        btnPrevious.setOnClickListener {
            if (page > 1) {
                page--
                getBlogs()
            }
        }
        btnNext.setOnClickListener {
            page++
            getBlogs()
        }
    }

    override fun onResume() {
        super.onResume()
        page = 1
        getBlogs()
    }

    private fun getBlogs() {
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
                            val pageMax = if(body.count % limit == 0) {
                                body.count / limit
                            } else {
                                body.count / limit + 1
                            }
                            btnPrevious.isEnabled = (page in 2 .. pageMax)
                            btnNext.isEnabled = (page in 1 until pageMax)
                            if(pageMax == 0) {
                                btnPrevious.isEnabled = false
                                btnNext.isEnabled = false
                            }
                            val blogAdapter = CustomAdminBlogItem(body.data as MutableList<BlogResponse>)

                            blogList.adapter = blogAdapter
                            blogList.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)

                            blogAdapter.onItemClick = {
                                val intent = Intent(this@BlogManagementActivity, BlogDetailActivity::class.java)
                                intent.putExtra("blogId", it.id)
                                intent.putExtra("activity", "management")
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