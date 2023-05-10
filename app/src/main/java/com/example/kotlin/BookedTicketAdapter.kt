package com.example.kotlin

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.kotlin.DataClass.HistoryItem

class BookedTicketAdapter
    (private val context: Activity, private var items: List<HistoryItem>)
    : ArrayAdapter<HistoryItem>(context, R.layout.item_ve, items){
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View{
            val inflater = context.layoutInflater
            val rowView: View = inflater.inflate(R.layout.item_ve, null, true)


            return rowView
        }

}