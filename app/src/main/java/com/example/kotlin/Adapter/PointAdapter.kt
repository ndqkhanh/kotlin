package com.example.kotlin.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlin.DataClass.Point
import com.example.kotlin.DataClass.ReviewItem
import com.example.kotlin.R
import com.facebook.login.widget.ProfilePictureView

class PointAdapter(private var items: MutableList<Point>): RecyclerView.Adapter<PointAdapter.PointViewHolder>(){
    inner class PointViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val ten = itemView.findViewById<TextView>(R.id.ten_diem)
        val diaChi = itemView.findViewById<TextView>(R.id.dia_chi_diem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.item_point, parent, false)

        return PointViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        val item = items[position]

        holder.diaChi.text = item.location
        holder.ten.text = item.name

    }

    override fun getItemCount(): Int {
        return items.size
    }
}