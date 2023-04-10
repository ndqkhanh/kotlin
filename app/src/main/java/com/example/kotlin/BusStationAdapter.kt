package com.example.kotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BusStationAdapter (val busStations:  MutableList<BusStation>): RecyclerView.Adapter<BusStationAdapter.ViewHolder>() {
    var onItemClick : ((BusStation) -> Unit)? = null
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView){
        val busStationTv = listItemView.findViewById<TextView>(R.id.busStationTv)
        init {
            listItemView.setOnClickListener { onItemClick?.invoke(busStations[adapterPosition]) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BusStationAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val busStationView = inflater.inflate(R.layout.bus_station_item,parent,false)
        return ViewHolder(busStationView)
    }

    override fun onBindViewHolder(holder: BusStationAdapter.ViewHolder, position: Int) {
        val busStation: BusStation = busStations.get(position)
        val busStationTv = holder.busStationTv

        busStationTv.setText(busStation.name + ", " + busStation.location)
    }

    override fun getItemCount(): Int {
        return busStations.size
    }


}