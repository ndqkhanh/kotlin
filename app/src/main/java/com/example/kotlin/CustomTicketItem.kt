package com.example.kotlin

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*


class Ticket {
    public var id: String = ""
    public var name: String = ""
    public var price: String = ""
    public var time: String = ""
    public var location: String = ""
    public var destination: String = ""

    constructor(name: String, price: String, time: String, location: String, destination: String) {
        this.id = UUID.randomUUID().toString()
        this.name = name
        this.price = price
        this.time = time
        this.location = location
        this.destination = destination
    }

    constructor(name: String, price: String, time: String, location: String, destination: String, id: String) {
        this.id = id
        this.name = name
        this.price = price
        this.time = time
        this.location = location
        this.destination = destination
    }
}

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


            busOperatorName.text = busses[position].bus_operators.name
            busPrice.text = busses[position].price.toString()
            busStartTime.text = busses[position].start_time
            busDeparture.text = busses[position].start_point.location
            busDestination.text = busses[position].end_point.location

            val detailsBtn = rowView.findViewById<Button>(R.id.details)
            detailsBtn.setOnClickListener {
                showBottomSheet?.invoke(busses[position])
            }

            return rowView
        }catch (e: Exception) {
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
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }
        val params = listView.layoutParams
        params.height = totalHeight + listView.dividerHeight * (listAdapter.count - 1)
        listView.layoutParams = params
    }
}
