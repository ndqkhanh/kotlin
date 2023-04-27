package com.example.kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.ListView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class BlogSeeAllActivity : AppCompatActivity() {
    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_see_all)

        /*Bottom navigation code*/
        // Initialize and assign variable
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        // Set Home selected
        bottomNavigationView.selectedItemId = R.id.blog
        // Perform item selected listener
        var intent: Intent
        bottomNavigationView.setOnNavigationItemSelectedListener{ menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    finish()
                    intent = Intent(this, HomePage::class.java)
                    startActivity(intent)
                    true
                }

                R.id.blog -> {
                    finish()
                    intent = Intent(this, BlogSeeAllActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.ticket -> {
                    finish()
                    intent = Intent(this, PersonalInformation::class.java)
                    startActivity(intent)
                    true
                }

                R.id.user -> {
                    finish()
                    intent = Intent(this, CaNhanActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }


        }



        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val lvTinTuc = findViewById<ListView>(R.id.lvTinTuc)
        val retrofit = APIServiceImpl()
        try {
            GlobalScope.launch(Dispatchers.IO) {
                val response =
                    retrofit.getBlog().getBlogs(1,20).awaitResponse()
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