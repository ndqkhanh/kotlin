package com.example.kotlin

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomBookingDetailAdapter(private val bookingDetails: MutableList<BookingDetail>) :
    RecyclerView.Adapter<CustomBookingDetailAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val txtTitle1: TextView = listItemView.findViewById(R.id.txtTitle1)
        val txtContent1: TextView = listItemView.findViewById(R.id.txtContent1)
        val txtTitle2: TextView = listItemView.findViewById(R.id.txtTitle2)
        val txtContent2: TextView = listItemView.findViewById(R.id.txtContent2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val bookingDetailView = inflater.inflate(R.layout.booking_detail_item, parent, false)
        return ViewHolder(bookingDetailView)
    }

    override fun getItemCount(): Int {
        return bookingDetails.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookingDetail = bookingDetails[position]
        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.color.white)
        } else {
            holder.itemView.setBackgroundResource(R.color.light_gray)
        }
        holder.txtTitle1.text = bookingDetail.title1
        holder.txtContent1.text = bookingDetail.content1
        holder.txtTitle2.text = bookingDetail.title2
        holder.txtContent2.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(bookingDetail.content2, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(bookingDetail.content2)
        }
    }
}