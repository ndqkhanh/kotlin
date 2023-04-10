package com.example.kotlin

import android.os.Bundle
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminActivity: AppCompatActivity() {
    var adapter: AdminGridAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val grid = findViewById<GridView>(R.id.adminGV)
        var adminItemList = generateCompanyData() //implemened below
        adapter = AdminGridAdapter(this, adminItemList)
        grid.adapter = adapter
        grid.setOnItemClickListener { adapterView, view, i, l ->
            Toast.makeText(
                this, " Selected Company is " + adminItemList.get(i).name,

                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun generateCompanyData(): ArrayList<AdminItem> {
        var result = ArrayList<AdminItem>()
        var adminItem: AdminItem = AdminItem()
        adminItem.id = 1
        adminItem.name = "Bus"
        adminItem.image = R.drawable.background_login // CHANGE LATER
        result.add(adminItem)

        adminItem = AdminItem()
        adminItem.id = 2
        adminItem.name = "Bus Booking"
        adminItem.image = R.drawable.background_login // CHANGE LATER
        result.add(adminItem)

        adminItem = AdminItem()
        adminItem.id = 3
        adminItem.name = "Bus Operator"
        adminItem.image = R.drawable.background_login // CHANGE LATER
        result.add(adminItem)

        adminItem = AdminItem()
        adminItem.id = 4
        adminItem.name = "Blog"
        adminItem.image = R.drawable.background_login // CHANGE LATER
        result.add(adminItem)

        return result
    }

}
