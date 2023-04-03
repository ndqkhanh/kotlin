package com.example.kotlin

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.InputStream
import java.net.URL
import java.util.*


class Blog {
    public var id: String = ""
    public var thumbnail: String = ""
    public var title: String = ""

    constructor(title: String, thumbnail: String) {
        this.id = UUID.randomUUID().toString()
        this.title = title
        this.thumbnail = thumbnail
    }

    constructor(title: String, thumbnail: String, id: String) {
        this.id = id
        this.title = title
        this.thumbnail = thumbnail
    }
}

class CustomBlogItem(private val blogs: ArrayList<Blog>): RecyclerView.Adapter<CustomBlogItem.ViewHolder>() {
    var onItemClick: ((Blog) -> Unit)? = null

    inner class ViewHolder(listItemView: View): RecyclerView.ViewHolder(listItemView) {
        val titleText = listItemView.findViewById(R.id.title) as TextView
        var thumbnail = listItemView.findViewById(R.id.thumbnail) as ImageView


        init {
            listItemView.setOnClickListener {
                onItemClick?.invoke(blogs[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val studentView = inflater.inflate(R.layout.activity_blog_item, parent, false)
        return ViewHolder(studentView)
    }

    override fun getItemCount(): Int {
        return blogs.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleText.text = blogs[position].title

        Glide.with(holder.thumbnail.context)
            .load(blogs[position].thumbnail)
            .into(holder.thumbnail)

    }

}