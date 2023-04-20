package com.example.kotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.GridView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminActivity: AppCompatActivity() {
    lateinit var backBtn: ImageButton
    var adapter: AdminGridAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val grid = findViewById<GridView>(R.id.adminGV)
        var adminItemList = generateCompanyData() //implemened below
        adapter = AdminGridAdapter(this, adminItemList)
        grid.adapter = adapter
        grid.setOnItemClickListener { adapterView, view, i, l ->
            when(adminItemList.get(i).name)
            {
                "Bus" -> Intent(this, AdminBusActivity::class.java).also {
                    startActivity(it)
                }
                "Bus Booking" -> Intent(this, AdminBusTicketActivity::class.java).also {
                    startActivity(it)
                }
                "Bus Operator" -> Intent(this, AdminBusOperatorActivity::class.java).also {
                    startActivity(it)
                }
                "Blog" -> Intent(this, BlogManagementActivity::class.java).also {
                    startActivity(it)
                }
            }
            Toast.makeText(
                this, " Selected Company is " + adminItemList.get(i).name,

                Toast.LENGTH_SHORT
            ).show()
        }



        backBtn = findViewById(R.id.adminPageBackBtn)
        backBtn.setOnClickListener {
            finish()
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
