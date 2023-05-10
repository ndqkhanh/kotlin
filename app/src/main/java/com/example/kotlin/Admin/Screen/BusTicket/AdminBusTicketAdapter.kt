package com.example.kotlin.Admin.Screen.BusTicket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlin.BusTicket
import com.example.kotlin.R
import com.example.kotlin.utils.dateFormat
import com.example.kotlin.utils.getDuration

class AdminBusTicketAdapter (private val busTickets: MutableList<BusTicket>)
    : RecyclerView.Adapter<AdminBusTicketAdapter.ViewHolder>() {
    var onButtonClick: ((BusTicket)->Unit)? = null
    inner class ViewHolder(listItemView : View): RecyclerView.ViewHolder(listItemView) {
//        val busIDVal = listItemView.findViewById<TextView>(R.id.adminBusTicketBusIDVal)
        val phoneVal = listItemView.findViewById<TextView>(R.id.adminBusTicketUserPhoneVal)
        val passengerVal = listItemView.findViewById<TextView>(R.id.adminBusTicketUserNameVal)
        val startPointVal = listItemView.findViewById<TextView>(R.id.adminBusTicketStartPointVal)
        val endPointVal = listItemView.findViewById<TextView>(R.id.adminBusTicketEndPointVal)
        val startTimeVal = listItemView.findViewById<TextView>(R.id.adminBusTicketStartTimeVal)
        val endTimeVal = listItemView.findViewById<TextView>(R.id.adminBusTicketEndTimeVal)
        val seatVal = listItemView.findViewById<TextView>(R.id.adminBusTicketSeatVal)
        val statusVal = listItemView.findViewById<TextView>(R.id.adminBusTicketStatusVal)
        val image = listItemView.findViewById<ImageView>(R.id.adminBusTicketImgV)
        val duration = listItemView.findViewById<TextView>(R.id.adminBusTicketDurationVal)
        val button = listItemView.findViewById<Button>(R.id.adminBusTicketDeleteBtn)

        init {
            val buttonTemp = listItemView.findViewById<Button>(R.id.adminBusTicketDeleteBtn)
            buttonTemp.setOnClickListener {
                onButtonClick?.invoke(busTickets[adapterPosition])
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
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
//        val busID = holder.busIDVal
        val phone = holder.phoneVal
        val passenger = holder.passengerVal
        val startPoint = holder.startPointVal
        val endPoint = holder.endPointVal
        val startTime = holder.startTimeVal
        val endTime = holder.endTimeVal
        val seat = holder.seatVal
        val status = holder.statusVal
        val image = holder.image
        val duration = holder.duration

//        busID.setText(busTicket.bus_id)
        passenger.setText(busTicket.name)
        startPoint.setText(busTicket.start_point)
        endPoint.setText(busTicket.end_point)
        phone.setText(busTicket.phone)
        startTime.setText(dateFormat(busTicket.start_time))
        endTime.setText(dateFormat(busTicket.end_time))
        seat.setText("Seat "+busTicket.seat)
        duration.setText(getDuration(busTicket.start_time, busTicket.end_time))
        when (busTicket.status)
        {
            "0" -> status.setText("Chưa thanh toán")
            "1" -> status.setText("Chờ thanh toán")
            "2" -> status.setText("Đã thanh toán")
        }
        image.setImageResource(R.drawable.ic_user_tie_solid)
    }
}