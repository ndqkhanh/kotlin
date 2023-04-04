package com.example.kotlin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class BlogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog)

        var blogs = ArrayList<Blog>()
        blogs.add(
            Blog(
                "Ninh bình 2",
                "https://cdn.pixabay.com/photo/2015/10/01/17/17/car-967387__480.png",
                "9dacf3c6-f73e-4218-a01d-097d8d3a7a20"
            )
        )
        blogs.add(
            Blog(
                "Ninh bình 3",
                "https://cdn.pixabay.com/photo/2015/10/01/17/17/car-967387__480.png",
                "310336cc-4250-4759-b182-4913d86af5c2"
            )
        )

        var blogList = findViewById<RecyclerView>(R.id.blogList)

        val blogAdapter = CustomBlogItem(blogs)

        blogList!!.adapter = blogAdapter
        blogList.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)

        blogAdapter.onItemClick = {
            val intent = Intent(this, BlogDetailActivity::class.java)
            intent.putExtra("blogId", it.id)
            startActivity(intent)
        }
    }
}