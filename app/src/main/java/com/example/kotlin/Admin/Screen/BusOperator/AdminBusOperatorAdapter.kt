package com.example.kotlin.Admin.Screen.BusOperator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlin.BusOperator
import com.example.kotlin.R

class AdminBusOperatorAdapter (private val busOperators: MutableList<BusOperator>)
    :RecyclerView.Adapter<AdminBusOperatorAdapter.ViewHolder>(){
    var onButtonClick: ((BusOperator)->Unit)? = null
    inner class ViewHolder (listItemView: View) : RecyclerView.ViewHolder (listItemView)
    {
        val name = listItemView.findViewById<TextView>(R.id.adminBusOperatorItemTitle)
        val phone = listItemView.findViewById<TextView>(R.id.adminBusOperatorPhoneItemVal)
        val image = listItemView.findViewById<ImageView>(R.id.adminBusOperatorImgV)

        val button = listItemView.findViewById<Button>(R.id.adminBusOperatorDeleteBtn)

        init {
            val buttonTemp = listItemView.findViewById<Button>(R.id.adminBusOperatorDeleteBtn)
            buttonTemp.setOnClickListener {
                onButtonClick?.invoke(busOperators[adapterPosition])
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val adminBusOperatorItemView = inflater.inflate(R.layout.admin_bus_operator_item,parent,false)
        return ViewHolder(adminBusOperatorItemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val busOperator: BusOperator = busOperators.get(position)
        val name = holder.name
        val phone = holder.phone
        val image = holder.image

        name.setText(busOperator.name)
        phone.setText(busOperator.phone)
        Glide.with(image.context)
            .load(busOperators[position].image_url)
            .into(image)
    }

    override fun getItemCount(): Int {
        return busOperators.size
    }

}
