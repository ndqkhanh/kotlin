package com.example.kotlin.User.Screen.BottomNavigate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.kotlin.APIServiceImpl
import com.example.kotlin.BlogDetailActivity
import com.example.kotlin.BlogSeeAllAdapter
import com.example.kotlin.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class BlogFragment : Fragment() {
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var retrofit: APIServiceImpl
    private lateinit var lvTinTuc: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_blog, container, false)
        lvTinTuc = rootView.findViewById(R.id.lvTinTuc)
        retrofit = APIServiceImpl()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                            val adapter = BlogSeeAllAdapter(requireContext() as Activity, body.data)
                            lvTinTuc.divider = null
                            lvTinTuc.dividerHeight = 0
                            lvTinTuc.adapter = adapter
                            lvTinTuc.onItemClickListener =
                                AdapterView.OnItemClickListener { _, _, position, _ ->
                                    val blog = body.data[position]
                                    val intent = Intent(requireContext(), BlogDetailActivity::class.java)
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