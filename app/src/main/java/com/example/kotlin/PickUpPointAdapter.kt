package com.example.kotlin

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class PickUpPointAdapter(private val activity: Activity, private val points: List<Point>) :
    ArrayAdapter<Point>(activity, R.layout.pick_up_drop_down_location_item, points) {
    private var selectedItem = -1
    fun setSelectedItem(position: Int) {
        selectedItem = position
        notifyDataSetChanged()
    }

    fun getSelectedPosition(): Int {
        return selectedItem
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = activity.layoutInflater
        val rowView = inflater.inflate(R.layout.pick_up_drop_down_location_item, parent, false)
        val cardView = rowView.findViewById<CardView>(R.id.CardView)
        val imgRadioButton = rowView.findViewById(R.id.imgRadioButton) as ImageView
        if (position == selectedItem) {
//            rowView.setBackgroundColor(Color.parseColor("#ECF3FD"))
            cardView.setCardBackgroundColor(Color.parseColor("#ECF3FD"))
            imgRadioButton.setImageResource(R.drawable.checked_radio)
        } else {
//            rowView.setBackgroundColor(Color.TRANSPARENT)
            cardView.setCardBackgroundColor(Color.TRANSPARENT)
            imgRadioButton.setImageResource(R.drawable.unchecked_radio)
        }
        val txtName = rowView.findViewById<TextView>(R.id.txtName)
        txtName.text = points[position].name
        val txtTime = rowView.findViewById<TextView>(R.id.txtTime)
        txtTime.text = points[position].time.split(" ")[0]
        val txtLocation = rowView.findViewById<TextView>(R.id.txtLocation)
        txtLocation.text = points[position].location
        return rowView
    }
}