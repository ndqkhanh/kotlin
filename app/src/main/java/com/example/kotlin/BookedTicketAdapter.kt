package com.example.kotlin

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.example.kotlin.jsonConvert.History

class BookedTicketAdapter
    (private val context: Activity, private var items: List<History>)
    : ArrayAdapter<History>(context, R.layout.item_ticket, items){
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View{
            val inflater = context.layoutInflater
            val rowView: View = inflater.inflate(R.layout.item_ticket, null, true)
            val imageTicket: ImageView = rowView.findViewById(R.id.imageTicket)
            imageTicket.setImageResource(R.drawable.ticket)

            return rowView
        }

}