package com.example.kotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.kotlin.Admin.Screen.AdminActivity
import com.example.kotlin.Admin.Screen.BottomNavigation.AdminFragment

class AdminItem {
    var id: Int? = 0
    var name: String? = null
    var image: Int? = null
}
class AdminGridAdapter(private var context: Context, private var items:
ArrayList<AdminItem>) : BaseAdapter(){
    private class ViewHolder(row: View?) {
        var nameTV: TextView? = null
        var imageView: ImageView? = null
        init {
            nameTV = row?.findViewById<TextView>(R.id.adminItemTV)
            imageView = row?.findViewById<ImageView>(R.id.adminItemImgV)
        }
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(p0: Int): Any {
        return items[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (p1 == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
            view = (inflater as LayoutInflater).inflate(R.layout.admin_list_item, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = p1
            viewHolder = view.tag as ViewHolder
        }

        var company = items[p0]
        viewHolder.nameTV?.text = company.name
        viewHolder.imageView?.setImageResource(company.image!!)
        return view as View
    }

}