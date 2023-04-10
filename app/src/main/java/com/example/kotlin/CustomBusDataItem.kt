package com.example.kotlin

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class CustomBusDataItem(private val texts: ArrayList<String>): RecyclerView.Adapter<CustomBusDataItem.ViewHolder>() {
    // constructor
    init {
        Log.d("Search", "vui ha" + texts.size)
    }
    inner class ViewHolder(listItemView: View): RecyclerView.ViewHolder(listItemView) {
        val columnText = listItemView.findViewById(R.id.columnText) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("Search", "vui sds")
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val studentView = inflater.inflate(R.layout.fragment_bus_information_column, parent, false)
        return ViewHolder(studentView)
    }

    @Override
    override fun getItemCount(): Int {
        Log.d("Search", "vui" + texts.size)
        return texts.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("Search", "vui1 + " + holder.columnText)
        Log.d("Search", "vui" + texts[position])
        holder.columnText.text = texts[position]
    }

}