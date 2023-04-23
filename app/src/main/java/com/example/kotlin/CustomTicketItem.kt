package com.example.kotlin

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.bumptech.glide.Glide
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


class CustomTicketItem(private val context: Activity, private val busses: List<Bus>, private val supportFragmentManager: FragmentManager, private val lifecycle: Lifecycle) : ArrayAdapter<Bus>(context,  R.layout.activity_ticket_item, busses) {
    var showBottomSheet: ((bus: Bus)->Unit)? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        try {
            val inflater = context.layoutInflater
            val rowView: View = inflater.inflate(R.layout.activity_ticket_item, null, true)
            val busOperatorName = rowView.findViewById(R.id.busOperatorName) as TextView
            val busPrice = rowView.findViewById(R.id.busPrice) as TextView
            val busStartTime = rowView.findViewById(R.id.busStartTime) as TextView
            val busDeparture = rowView.findViewById(R.id.busDeparture) as TextView
            val busDestination = rowView.findViewById(R.id.busDestination) as TextView
            val busImage = rowView.findViewById(R.id.busImage) as ImageView

            busOperatorName.text = busses[position].bus_operators.name
            val pricingText: String = "Pricing: " + busses[position].pricing_format;
            busPrice.text = pricingText
            val timeText: String = "Time: " + busses[position].duration
            busStartTime.text = timeText
            val departureText: String = "Departure: " + busses[position].start_point.location + " - " + busses[position].start_time
            busDeparture.text = departureText
            val destinationText: String = "Destination: " + busses[position].end_point.location + " - " + busses[position].end_time
            busDestination.text = destinationText
            busOperatorName.text = busses[position].bus_operators.name

            Glide.with(busImage.context)
                .load(busses[position].image_url)
                .into(busImage)

            val detailsBtn = rowView.findViewById<Button>(R.id.details)
            detailsBtn.setOnClickListener {
                showBottomSheet?.invoke(busses[position])
            }
            val buyTicketBtn = rowView.findViewById<Button>(R.id.buyTicket)



            return rowView
        } catch (e: Exception) {
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
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }
        val params = listView.layoutParams
        params.height = totalHeight + listView.dividerHeight * (listAdapter.count - 1)
        listView.layoutParams = params
    }
}
