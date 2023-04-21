package com.example.kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton

class ChooseDropDownLocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_pick_up_location)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val busPickUpPoints = ArrayList<Point>()
        busPickUpPoints.add(Point("1","8:00", "Point 1", "Location 1"))
        busPickUpPoints.add(Point("2","8:30", "Point 2", "Location 2"))
        busPickUpPoints.add(Point("3","9:00", "Point 3", "Location 3"))


        val listView = findViewById<ListView>(R.id.lvDiemDon)
        val adapter = PickUpPointAdapter(this, busPickUpPoints)
        listView.adapter = adapter
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ -> adapter.setSelectedItem(position) }

        val continueBtn = findViewById<AppCompatButton>(R.id.continueBtn)
        continueBtn.setOnClickListener {
            if(adapter.getSelectedPosition() == -1) {
                Toast.makeText(this, "Hãy chọn điếm trả bạn muốn", Toast.LENGTH_SHORT).show()
            }
            else {
                intent = Intent(this, EnterInformationActiviy::class.java)
                startActivity(intent)
            }
        }
    }
}