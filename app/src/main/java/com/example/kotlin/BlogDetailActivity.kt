package com.example.kotlin

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_detail)

        imgBlogDetail = findViewById(R.id.imgBlogDetail)
        txtTitle = findViewById(R.id.txtTitle)
        txtContent = findViewById(R.id.txtContent)
        txtCreateTime = findViewById(R.id.txtCreateTime)
        btnDelete = findViewById(R.id.btnDelete)

        val retrofit = APIServiceImpl()

        val blogId = "310336cc-4250-4759-b182-4913d86af5c2"
        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJjMTE4ZjY5My04NzIyLTQ0NjEtYTc5ZC1kNzY5OTFiOTZiY2QiLCJpYXQiOjE2ODAwOTQ4MjUsImV4cCI6MTY4MDA5NjYyNSwidHlwZSI6ImFjY2VzcyJ9.CisI-dPGSQEsysUu-eVXqDkR5CIIY-mFuL52byTlIGY"

        GlobalScope.launch(Dispatchers.IO) {
            val response =
                retrofit.getBlog().getBlogById(blogId).awaitResponse()
            // debug response
            Log.d("Response", response.toString())
            if (response.isSuccessful) {
                Log.d("Response", response.body().toString())
                launch(Dispatchers.Main) {
                    val blog = response.body()
                    txtTitle.text = blog?.title
                    txtContent.text = blog?.content
                    txtCreateTime.text = blog?.created_time
//                    imgBlogDetail.setImageBitmap(getBitmapFromURL(url))
                }
            }
        }

        btnDelete.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val response =
                    retrofit.manipulateBlog(token).deleteBlogById(blogId).awaitResponse()
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
    }
}