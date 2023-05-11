package com.example.kotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BusOperatorAdapter(val busOperators: MutableList<BusOperator>): RecyclerView.Adapter<BusOperatorAdapter.ViewHolder>() {
    var onItemClick : ((BusOperator) -> Unit)? = null
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView){
        val busOperatorTv = listItemView.findViewById<TextView>(R.id.busOperatorTv)
        init {
            listItemView.setOnClickListener { onItemClick?.invoke(busOperators[adapterPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusOperatorAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val busOperatorView = inflater.inflate(R.layout.bus_operator_item,parent,false)
        return ViewHolder(busOperatorView)
    }

    override fun onBindViewHolder(holder: BusOperatorAdapter.ViewHolder, position: Int) {
        val busOperator: BusOperator = busOperators.get(position)
        val busOperatorTv = holder.busOperatorTv

        busOperatorTv.setText(busOperator.name)
    }

    override fun getItemCount(): Int {
        return busOperators.size
    }


}