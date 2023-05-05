package com.example.kotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class BlogManagementActivity : AppCompatActivity() {
    val retrofit = APIServiceImpl()
    private var page = 1
    private val limit = 20
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_management)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val btnAddBlog = findViewById<Button>(R.id.btnAddBlog)
        btnAddBlog.setOnClickListener {
            val intent = Intent(this, AdminBlogCreateActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
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
                            val blogAdapter = BlogManagementAdapter(this@BlogManagementActivity, body.data)
                            val lvTinTuc = findViewById<ListView>(R.id.lvTinTuc)
                            lvTinTuc.divider = null
                            lvTinTuc.dividerHeight = 0
                            lvTinTuc.adapter = blogAdapter
                            // get list title from body.data
                            val listTitle = body.data.map { it.title }
                            val autoTinTuc = findViewById<AutoCompleteTextView>(R.id.autoTinTuc)
                            val adapter = ArrayAdapter(
                                this@BlogManagementActivity,
                                android.R.layout.simple_list_item_1,
                                listTitle
                            )
                            autoTinTuc.setAdapter(adapter)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
    }
}