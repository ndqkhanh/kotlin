package com.example.kotlin.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlin.DataClass.ReviewItem
import com.example.kotlin.R
import com.facebook.login.widget.ProfilePictureView

class ReviewAdapter(private var items: MutableList<ReviewItem>): RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>(){

    inner class ReviewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val ten = itemView.findViewById<TextView>(R.id.user_review_name)
        val ratingBar = itemView.findViewById<RatingBar>(R.id.ratingBar)
        val review = itemView.findViewById<TextView>(R.id.user_review)
        val ngay_dang = itemView.findViewById<TextView>(R.id.ngay_dang_review)
        val normal_avt = itemView.findViewById<ImageView>(R.id.normal_avt_user_review)
        val fb_avt = itemView.findViewById<ProfilePictureView>(R.id.fb_avt_user_review)
        val fb_card = itemView.findViewById<CardView>(R.id.fb_avt_user_review_card)
        val normal_card = itemView.findViewById<CardView>(R.id.normal_avt_user_review_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.item_danh_gia, parent, false)

        return ReviewViewHolder(contactView)

    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val item = items[position]
        holder.ngay_dang.text = item.ngay_review
        holder.ratingBar.rating = item.rate.toFloat()
        holder.review.text = item.comment
        holder.ten.text = item.name

        if(item.avatar_url != null){
            holder.fb_card.visibility = View.INVISIBLE
            holder.normal_card.visibility = View.VISIBLE
            Glide.with(holder.itemView).load(item.avatar_url).into(holder.normal_avt)
        }else{
            holder.fb_avt.profileId = item.account_name
            holder.fb_card.visibility = View.VISIBLE
            holder.normal_card.visibility = View.INVISIBLE
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }
}