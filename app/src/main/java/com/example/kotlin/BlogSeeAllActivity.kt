package com.example.kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ListView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class BlogSeeAllActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_see_all)

        val lvTinTuc = findViewById<ListView>(R.id.lvTinTuc)
        val retrofit = APIServiceImpl()
        try {
            GlobalScope.launch(Dispatchers.IO) {
                val response =
                    retrofit.getBlog().getBlogs(1,5).awaitResponse()
                // debug response
                Log.d("Response", response.toString())
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("Response", body.toString())
                    launch(Dispatchers.Main) {
                        if (body != null && body.count > 0) {
                            val adapter = BlogSeeAllAdapter(this@BlogSeeAllActivity, body.data)
                            lvTinTuc.adapter = adapter
                            lvTinTuc.onItemClickListener =
                                AdapterView.OnItemClickListener { _, _, position, _ ->
                                    val blog = body.data[position]
                                    val intent = Intent(this@BlogSeeAllActivity, BlogDetailActivity::class.java)
                                    intent.putExtra("blogId", blog.id)
                                    startActivity(intent)
                                }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}