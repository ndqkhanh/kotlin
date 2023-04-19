package com.example.kotlin

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.kotlin.jsonConvert.History

class BookedTicketAdapter
    (private val context: Activity, private var items: List<History>)
    : ArrayAdapter<History>(context, R.layout.item_ticket, items){
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View{
            val inflater = context.layoutInflater
            val rowView: View = inflater.inflate(R.layout.item_ticket, null, true)
            val imageTicket: ImageView = rowView.findViewById(R.id.imageTicket)
            imageTicket.setImageResource(R.drawable.ticket)

            val startPoint = rowView.findViewById<TextView>(R.id.startPoint)
            val startTime = rowView.findViewById<TextView>(R.id.startTime)
            val endPoint = rowView.findViewById<TextView>(R.id.endPoint)
            val endTime = rowView.findViewById<TextView>(R.id.endTime)
            val id = rowView.findViewById<TextView>(R.id.ticketHID)
            val bh = rowView.findViewById<TextView>(R.id.ticketHBH)

            var ticket = items[position]

            startPoint.text = ticket.buses!!.bus_stations_bus_stationsTobuses_start_point.location
            endPoint.text = ticket.buses!!.bus_stations_bus_stationsTobuses_end_point.location
            startTime.text = ticket.buses!!.start_time
            endTime.text = ticket.buses!!.end_time
            id.text = ticket.id
            bh.text = ticket.buses!!.bus_operators.name

            return rowView
        }

}