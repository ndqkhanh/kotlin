package com.example.kotlin

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.kotlin.User.Screen.BottomNavigate.BlogFragment

class BlogSeeAllAdapter(private val activity: Activity, private val blogs: List<BlogResponse>) :
    ArrayAdapter<BlogResponse>(activity, R.layout.blog_see_all_item, blogs) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = activity.layoutInflater
        val rowView = inflater.inflate(R.layout.blog_see_all_item, parent, false)
        val imgThumbnail = rowView.findViewById(R.id.imgThumbnail) as ImageView
        val txtTitle = rowView.findViewById(R.id.txtTitle) as TextView
        txtTitle.text = blogs[position].title
        Glide.with(imgThumbnail.context)
            .load(blogs[position].thumbnail)
            .into(imgThumbnail)
        return rowView
    }
}