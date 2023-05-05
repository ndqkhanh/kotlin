package com.example.kotlin

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class BlogManagementAdapter(private val activity: Activity, private val blogs: List<BlogResponse>) :
    ArrayAdapter<BlogResponse>(activity, R.layout.blog_management_item, blogs) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = activity.layoutInflater
        val rowView = inflater.inflate(R.layout.blog_management_item, parent, false)
        val imgThumbnail = rowView.findViewById(R.id.imgThumbnail) as ImageView
        val txtTitle = rowView.findViewById(R.id.txtTitle) as TextView
        val txtUpdateTime = rowView.findViewById(R.id.txtUpdateTime) as TextView
        val btnViewDetail = rowView.findViewById(R.id.btnViewDetail) as Button
        txtTitle.text = blogs[position].title
        Glide.with(imgThumbnail.context)
            .load(blogs[position].thumbnail)
            .into(imgThumbnail)
        txtUpdateTime.text = blogs[position].update_time
        btnViewDetail.setOnClickListener {
            val intent = Intent(activity, BlogManagementDetailActivity::class.java)
            intent.putExtra("blogId", blogs[position].id)
            activity.startActivity(intent)
        }
        return rowView
    }
}