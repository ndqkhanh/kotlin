package com.example.kotlin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlin.Admin.Screen.AdminBlogCreateActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import java.util.*


class BlogManagementActivity : AppCompatActivity() {
    val retrofit = APIServiceImpl()
    private lateinit var lvTinTuc: ListView
    private lateinit var blogAdapter: BlogManagementAdapter
    private lateinit var autoTinTuc: AutoCompleteTextView
    private var listBlog = mutableListOf<BlogResponse>()
    private var page = 1
    private val limit = 20
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_management)

        lvTinTuc = findViewById(R.id.lvTinTuc)
        autoTinTuc = findViewById(R.id.autoTinTuc)
        autoTinTuc.text = null

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val btnAddBlog = findViewById<Button>(R.id.btnAddBlog)
        btnAddBlog.setOnClickListener {
            val intent = Intent(this, AdminBlogCreateActivity::class.java)
            startActivity(intent)
        }

//        fetchData()
//        getBlogs()
//        blogAdapter = BlogManagementAdapter(this@BlogManagementActivity, listBlog as ArrayList<BlogResponse>)
//        lvTinTuc = findViewById(R.id.lvTinTuc)
//        lvTinTuc.divider = null
//        lvTinTuc.dividerHeight = 0
//        lvTinTuc.adapter = blogAdapter
//
//        // get list title from body.data
//        val listTitle = listBlog.map { it.title }
//        val autoTinTuc = findViewById<AutoCompleteTextView>(R.id.autoTinTuc)
//        val adapter = ArrayAdapter(
//            this@BlogManagementActivity,
//            android.R.layout.simple_list_item_1,
//            listTitle
//        )
//        autoTinTuc.setAdapter(adapter)
//        autoTinTuc.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//            }
//
//            override fun beforeTextChanged(
//                s: CharSequence?,
//                start: Int,
//                count: Int,
//                after: Int
//            ) {
//            }
//
//            override fun onTextChanged(
//                s: CharSequence?,
//                start: Int,
//                before: Int,
//                count: Int
//            ) {
//                blogAdapter =
//                    BlogManagementAdapter(
//                        this@BlogManagementActivity,
//                        getBlogListContainNameIgnoseCase(s.toString()) as ArrayList<BlogResponse>
//                    )
//                lvTinTuc.adapter = blogAdapter
//            }
//        })
    }

    override fun onResume() {
        super.onResume()
        autoTinTuc.text = null
        autoTinTuc.clearFocus()
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
                            listBlog = body.data as MutableList<BlogResponse>
                            blogAdapter = BlogManagementAdapter(this@BlogManagementActivity, listBlog)
                            lvTinTuc.divider = null
                            lvTinTuc.dividerHeight = 0
                            lvTinTuc.adapter = blogAdapter
                            // get list title from body.data
                            val listTitle = listBlog.map { it.title }
                            val adapter = ArrayAdapter(
                                this@BlogManagementActivity,
                                android.R.layout.simple_list_item_1,
                                listTitle
                            )
                            autoTinTuc.setAdapter(adapter)
                            autoTinTuc.addTextChangedListener(object : TextWatcher {
                                override fun afterTextChanged(s: Editable?) {
                                }

                                override fun beforeTextChanged(
                                    s: CharSequence?,
                                    start: Int,
                                    count: Int,
                                    after: Int
                                ) {
                                }

                                override fun onTextChanged(
                                    s: CharSequence?,
                                    start: Int,
                                    before: Int,
                                    count: Int
                                ) {
                                    blogAdapter =
                                        BlogManagementAdapter(
                                            this@BlogManagementActivity,
                                            getBlogListContainNameIgnoseCase(s.toString()) as ArrayList<BlogResponse>
                                        )
                                    lvTinTuc.adapter = blogAdapter
                                }
                            })
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
    }

    fun getBlogListContainNameIgnoseCase(name: String): MutableList<BlogResponse> {
        val blogListByTitle: MutableList<BlogResponse> = mutableListOf()
        for (blog in listBlog) {
            val nameSplit = name.split(" ")
            if (nameSplit.size > 1) {
                if (blog.title.lowercase(Locale.getDefault())
                        .startsWith(name.lowercase(Locale.getDefault()))
                ) {
                    blogListByTitle.add(blog)
                }
            } else if (nameSplit.size == 1) {
                val nameSplitBySpace = blog.title.split(" ")
                for (n in nameSplitBySpace) {
                    if (n.lowercase(Locale.getDefault())
                            .startsWith(name.lowercase(Locale.getDefault()))
                    ) {
                        blogListByTitle.add(blog)
                        break
                    }
                }
            }
        }
        return blogListByTitle
    }
}