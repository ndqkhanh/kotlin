package com.example.kotlin

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
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
        val imgRadioButton = rowView.findViewById(R.id.imgRadioButton) as ImageView
        if (position == selectedItem) {
            rowView.setBackgroundColor(Color.parseColor("#ECF3FD"))
            imgRadioButton.setImageResource(R.drawable.checked_radio)
        } else {
            rowView.setBackgroundColor(Color.TRANSPARENT)
            imgRadioButton.setImageResource(R.drawable.unchecked_radio)
        }
        val txtName = rowView.findViewById<TextView>(R.id.txtName)
        txtName.text = points[position].name
        return rowView
    }
}

//class PickUpPointAdapter(private val points: List<Point>): RecyclerView.Adapter<PickUpPointAdapter.ViewHolder>() {
//    var onItemClick: ((Point) -> Unit)? = null
//    private var selectedItem = -1
//
//    fun setSelectedItem(position: Int) {
//        selectedItem = position
//        notifyDataSetChanged()
//    }
//
//    fun getSelectedPosition(): Int {
//        return selectedItem
//    }
//
//    inner class ViewHolder(listItemView: View): RecyclerView.ViewHolder(listItemView) {
//        val constraintLayout = listItemView.findViewById(R.id.constraintLayout) as ConstraintLayout
//        val imgRadioButton = listItemView.findViewById(R.id.imgRadioButton) as ImageView
//        val txtTime = listItemView.findViewById(R.id.txtTime) as TextView
//        val txtName = listItemView.findViewById(R.id.txtName) as TextView
//        var txtLocation = listItemView.findViewById(R.id.txtLocation) as TextView
//
//        init {
//            listItemView.setOnClickListener {
//                onItemClick?.invoke(points[adapterPosition])
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val context = parent.context
//        val inflater = LayoutInflater.from(context)
//        val studentView = inflater.inflate(R.layout.activity_blog_item, parent, false)
//        return ViewHolder(studentView)
//    }
//
//    override fun getItemCount(): Int {
//        return points.size
//    }
//
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.txtTime.text = points[position].time
//        holder.txtName.text = points[position].name
//        holder.txtLocation.text = points[position].location
//
//        if (position == selectedItem) {
//            holder.imgRadioButton.setImageResource(R.drawable.checked_radio)
//            holder.constraintLayout.setBackgroundColor(Color.parseColor("#ECF3FD"))
//        } else {
//            holder.imgRadioButton.setImageResource(R.drawable.unchecked_radio)
//            holder.constraintLayout.setBackgroundColor(Color.TRANSPARENT)
//        }
//    }
//}