package com.example.kotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin.jsonConvert.Buses

class AdminBusAdapter(val buses: MutableList<Buses>): RecyclerView.Adapter<AdminBusAdapter.ViewHolder>() {
    var onButtonClick: ((Buses)->Unit)? = null
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView){
        val busOperator = listItemView.findViewById<TextView>(R.id.adminBusBOItemTitle)
        val startPoint = listItemView.findViewById<TextView>(R.id.adminBusStartPointVal)
        val endPoint = listItemView.findViewById<TextView>(R.id.adminBusEndPointVal)
        val startTime = listItemView.findViewById<TextView>(R.id.adminBusStartTimeVal)
        val endTime = listItemView.findViewById<TextView>(R.id.adminBusEndTimeVal)
        val type = listItemView.findViewById<TextView>(R.id.adminBusTypeVal)
        val price = listItemView.findViewById<TextView>(R.id.adminBusPriceVal)
//        val image = listItemView.findViewById<ImageView>(R.id.adminBusImgV)

        init {
            val buttonTemp = listItemView.findViewById<Button>(R.id.adminBusDeleteItemBtn)
            buttonTemp.setOnClickListener {
                onButtonClick?.invoke(buses[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val adminBusView = inflater.inflate(R.layout.admin_bus_list_item,parent,false)
        return ViewHolder(adminBusView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bus: Buses = buses.get(position)
        val busOperator = holder.busOperator
        val startPoint = holder.startPoint
        val endPoint = holder.endPoint
        val startTime = holder.startTime
        val endTime = holder.endTime
        val type = holder.type
        val price = holder.price
//        val image = holder.image

        // TODO Modify to see correct
        busOperator.setText(bus.bus_operators.name)
        startPoint.setText(bus.bus_stations_bus_stationsTobuses_start_point.name)
        endPoint.setText(bus.bus_stations_bus_stationsTobuses_end_point.name)
        startTime.setText(bus.start_time)
        endTime.setText(bus.end_time)
        when (bus.type)
        {
            0 ->  type.setText("Ghế ngồi")
            1 ->  type.setText("Giường nằm")
            2 ->  type.setText("Giường nằm đôi")
        }
//        type.setText(bus.type.toString())
        price.setText("VND " + bus.price.toString())
//        image.setImageResource(android.R.drawable.star_big_on)

    }

    override fun getItemCount(): Int {
        return buses.size
    }
}