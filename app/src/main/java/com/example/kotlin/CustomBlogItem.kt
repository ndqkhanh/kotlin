package com.example.kotlin

import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse


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
class CustomAdminBlogItem(private val blogs: MutableList<BlogResponse>): RecyclerView.Adapter<CustomAdminBlogItem.ViewHolder>() {
    var onItemClick: ((BlogResponse) -> Unit)? = null

    inner class ViewHolder(listItemView: View): RecyclerView.ViewHolder(listItemView) {
        val titleText = listItemView.findViewById(R.id.txtTitle) as TextView
        var thumbnail = listItemView.findViewById(R.id.thumbnail) as ImageView
        val txtContent = listItemView.findViewById(R.id.txtContent) as TextView
        val txtCreateTime = listItemView.findViewById(R.id.txtCreateTime) as TextView
//        val btnDelete = listItemView.findViewById(R.id.btnDelete) as Button

        init {
            listItemView.setOnClickListener {
                onItemClick?.invoke(blogs[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val studentView = inflater.inflate(R.layout.blog_see_all_item, parent, false)
        return ViewHolder(studentView)
    }

    override fun getItemCount(): Int {
        return blogs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleText.text = blogs[position].title
        holder.txtContent.text = blogs[position].content
        holder.txtCreateTime.text = blogs[position].create_time
        val retrofit = APIServiceImpl()
        val token = FBInfor.TOKEN
//        holder.btnDelete.setOnClickListener {
//            try {
//                if (FBInfor.ROLE != 0 && FBInfor.ROLE != 1) {
//                    return@setOnClickListener
//                }
//                val dialog = AlertDialog.Builder(holder.btnDelete.context)
//                dialog.setTitle("Delete Blog")
//                dialog.setMessage("Are you sure you want to delete this blog?")
//                dialog.setPositiveButton("Yes") { _, _ ->
//                    GlobalScope.launch(Dispatchers.IO) {
//                        val response =
//                            retrofit.manipulateBlog(token!!).deleteBlogById(blogs[position].id!!)
//                                .awaitResponse()
//                        // debug response
//                        Log.d("Response", response.toString())
//                        if (response.isSuccessful) {
//                            Log.d("Response", response.body().toString())
//                            launch(Dispatchers.Main) {
//                                blogs.removeAt(position)
//                                notifyItemRemoved(position)
//                            }
//                        }
//                    }
//                }
//                dialog.setNegativeButton("No") { dialogInterface: DialogInterface, _ ->
//                    dialogInterface.dismiss()
//                }
//                dialog.show()
//            } catch (e: Exception) {
//                Log.d("Error", e.toString())
//            }
//        }

        Glide.with(holder.thumbnail.context)
            .load(blogs[position].thumbnail)
            .into(holder.thumbnail)
    }
}

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