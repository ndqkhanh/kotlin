package com.example.kotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


//class Blog {
//    public var id: String = ""
//    public var thumbnail: String = ""
//    public var title: String = ""
//
//    constructor(title: String, thumbnail: String) {
//        this.id = UUID.randomUUID().toString()
//        this.title = title
//        this.thumbnail = thumbnail
//    }
//
//    constructor(title: String, thumbnail: String, id: String) {
//        this.id = id
//        this.title = title
//        this.thumbnail = thumbnail
//    }
//}
//class Blog {
//    public var id: String = ""
//    public var thumbnail: String = ""
//    public var title: String = ""
//
//    constructor(title: String, thumbnail: String) {
//        this.id = UUID.randomUUID().toString()
//        this.title = title
//        this.thumbnail = thumbnail
//    }
//
//    constructor(title: String, thumbnail: String, id: String) {
//        this.id = id
//        this.title = title
//        this.thumbnail = thumbnail
//    }
//}
//

class CustomBlogItem(private val blogs: List<BlogResponse>): RecyclerView.Adapter<CustomBlogItem.ViewHolder>() {
    var onItemClick: ((BlogResponse) -> Unit)? = null

    inner class ViewHolder(listItemView: View): RecyclerView.ViewHolder(listItemView) {
        val titleText = listItemView.findViewById(R.id.news_item_title) as TextView
        var thumbnail = listItemView.findViewById(R.id.news_item_thumbnail) as ImageView

        init {
            listItemView.setOnClickListener {
                onItemClick?.invoke(blogs[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val studentView = inflater.inflate(R.layout.fragment_news_item, parent, false)
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