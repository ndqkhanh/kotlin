package com.example.kotlin

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.facebook.appevents.ml.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import android.text.Html
import androidx.core.text.HtmlCompat

class BlogDetailActivity : AppCompatActivity() {
    private lateinit var imgBlogDetail: ImageView
    private lateinit var txtTitle: TextView
    private lateinit var txtContent: TextView
    private lateinit var txtUpdateTime: TextView
    private lateinit var btnBack: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_detail)

        imgBlogDetail = findViewById(R.id.imgBlogDetail)
        txtTitle = findViewById(R.id.txtTitle)
        txtContent = findViewById(R.id.txtContent)
        txtUpdateTime = findViewById(R.id.txtUpdateTime)
        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
        val blogId = intent.getStringExtra("blogId")
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
                        val htmlContent = blog?.content
                        txtContent.text = htmlContent?.let {
                            HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        }
                        txtUpdateTime.text = blog?.update_time
                        Glide.with(imgBlogDetail.context)
                            .load(blog?.thumbnail)
                            .into(imgBlogDetail)
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
    }
}