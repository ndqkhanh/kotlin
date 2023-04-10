package com.example.kotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminBusTicketAdapter (private val busTickets: MutableList<BusTicket>)
    : RecyclerView.Adapter<AdminBusTicketAdapter.ViewHolder>() {
    var onButtonClick: ((BusTicket)->Unit)? = null
    inner class ViewHolder(listItemView : View): RecyclerView.ViewHolder(listItemView) {
        val busIDVal = listItemView.findViewById<TextView>(R.id.adminBusTicketBusIDVal)
        val passengerVal = listItemView.findViewById<TextView>(R.id.adminBusTicketUserNameVal)
        val startPointVal = listItemView.findViewById<TextView>(R.id.adminBusTicketStartPointVal)
        val endPointVal = listItemView.findViewById<TextView>(R.id.adminBusTicketEndPointVal)
        val timeVal = listItemView.findViewById<TextView>(R.id.adminBusTicketTimeVal)
        val seatVal = listItemView.findViewById<TextView>(R.id.adminBusTicketSeatVal)
        val statusVal = listItemView.findViewById<TextView>(R.id.adminBusTicketStatusVal)
        val image = listItemView.findViewById<ImageView>(R.id.adminBusTicketImgV)

        val button = listItemView.findViewById<Button>(R.id.adminBusTicketDeleteBtn)

        init {
            val buttonTemp = listItemView.findViewById<Button>(R.id.adminBusTicketDeleteBtn)
            buttonTemp.setOnClickListener {
                onButtonClick?.invoke(busTickets[adapterPosition])
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminBusTicketAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val adminBusItemView = inflater.inflate(R.layout.admin_bus_ticket_item,parent,false)
        return ViewHolder(adminBusItemView)
    }

    override fun getItemCount(): Int {
        return busTickets.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val busTicket: BusTicket = busTickets.get(position)
        val busID = holder.busIDVal
        val passenger = holder.passengerVal
        val startPoint = holder.startPointVal
        val endPoint = holder.endPointVal
        val time = holder.timeVal
        val seat = holder.seatVal
        val status = holder.statusVal
        val image = holder.image

        busID.setText(busTicket.bus_id)
        passenger.setText(busTicket.name)
        startPoint.setText(busTicket.start_point)
        endPoint.setText(busTicket.end_point)
        time.setText(busTicket.time)
        seat.setText(busTicket.seat)
        status.setText(busTicket.status)
        image.setImageResource(android.R.drawable.star_big_on)
    }
}