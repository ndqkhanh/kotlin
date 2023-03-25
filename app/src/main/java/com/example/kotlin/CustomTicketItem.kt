package com.example.kotlin

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import java.util.*


class Ticket {
    public var id: String = ""
    public var name: String = ""
    public var price: String = ""
    public var time: String = ""
    public var location: String = ""
    public var destination: String = ""

    constructor(name: String, price: String, time: String, location: String, destination: String) {
        this.id = UUID.randomUUID().toString()
        this.name = name
        this.price = price
        this.time = time
        this.location = location
        this.destination = destination
    }

    constructor(name: String, price: String, time: String, location: String, destination: String, id: String) {
        this.id = id
        this.name = name
        this.price = price
        this.time = time
        this.location = location
        this.destination = destination
    }
}

class CustomTicketItem(private val context: Activity, private val tickets: ArrayList<Ticket>) : ArrayAdapter<Ticket>(context,  R.layout.activity_ticket_item, tickets) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        try {
            Log.d("debug", "name: " + tickets[position].name)
            Log.d("debug", "name: " + tickets[position].price)

            val inflater = context.layoutInflater
            val rowView: View = inflater.inflate(R.layout.activity_ticket_item, null, true)
            val ticketName = rowView.findViewById(R.id.ticketName) as TextView
            val ticketPrice = rowView.findViewById(R.id.ticketPrice) as TextView
            val ticketTime = rowView.findViewById(R.id.ticketTime) as TextView
            val ticketLocation = rowView.findViewById(R.id.ticketLocation) as TextView
            val ticketDestination = rowView.findViewById(R.id.ticketDestination) as TextView

            ticketName.text = tickets[position].name
            ticketPrice.text = tickets[position].price
            ticketTime.text = tickets[position].time
            ticketLocation.text = tickets[position].location
            ticketDestination.text = tickets[position].destination
            return rowView
        }catch (e: Exception) {
            Log.d("debug", "test")
            e.printStackTrace()
        }
        return super.getView(position, convertView, parent)
    }


}

object Utility {
    fun setListViewHeightBasedOnChildren(listView: ListView) {
        val listAdapter = listView.adapter
            ?: // pre-condition
            return
        var totalHeight = listView.paddingTop + listView.paddingBottom
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            (listItem as? ViewGroup)?.layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }
        val params = listView.layoutParams
        params.height = totalHeight + listView.dividerHeight * (listAdapter.count - 1)
        listView.layoutParams = params
    }
}
