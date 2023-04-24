package com.example.kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class Point(val id: String, val time: String, val name: String, val location: String)

class ChoosePickUpLocationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_pick_up_location)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val retrofit = APIServiceImpl()

        val busId = "ffe0ffaa-7d1c-4a2e-b812-46674ba8c85d";

        val busPickUpPoints = ArrayList<Point>()

        try {
            GlobalScope.launch(Dispatchers.IO) {
                val response =
                    retrofit.bus().getBusById(busId).awaitResponse()
                if (response.isSuccessful){
                    val body = response.body()
                    Log.i("body", body.toString())
                    launch(Dispatchers.Main) {
                        if (body != null) {
                            val txtBusOperatorName = findViewById<TextView>(R.id.txtBusOperatorName)
                            txtBusOperatorName.text = body.bus_operators.name
                            val txtTime = findViewById<TextView>(R.id.txtTime)
                            txtTime.text = body.start_time
                            try {
                                GlobalScope.launch(Dispatchers.IO) {
                                    val response2 =
                                        retrofit.point().getPointsByBsId(body.start_point.id).awaitResponse()
                                    if (response2.isSuccessful) {
                                        val body2 = response2.body()
                                        launch(Dispatchers.Main) {
                                            if (body2 != null) {
                                                for (i in 0 until body2.data.size) {
                                                    busPickUpPoints.add(Point(body2.data[i].point_id, body.start_time,
                                                        body2.data[i].points.name, body2.data[i].points.location))
                                                }
                                                val listView = findViewById<ListView>(R.id.lvDiemDon)
                                                val adapter = PickUpPointAdapter(this@ChoosePickUpLocationActivity, busPickUpPoints)
                                                listView.adapter = adapter
                                                listView.onItemClickListener =
                                                    AdapterView.OnItemClickListener { _, _, position, _ -> adapter.setSelectedItem(position) }

                                                val continueBtn = findViewById<AppCompatButton>(R.id.continueBtn)
                                                continueBtn.setOnClickListener {
                                                    if(adapter.getSelectedPosition() == -1) {
                                                        Toast.makeText(this@ChoosePickUpLocationActivity, "Hãy chọn điếm đón bạn muốn", Toast.LENGTH_SHORT).show()
                                                    }
                                                    else {
                                                        intent = Intent(this@ChoosePickUpLocationActivity, ChooseDropDownLocationActivity::class.java)
                                                        intent.putExtra("busId", busId)
                                                        intent.putExtra("busPickUpPointId", busPickUpPoints[adapter.getSelectedPosition()].id)
                                                        intent.putExtra("busPickUpPointName", busPickUpPoints[adapter.getSelectedPosition()].name)
                                                        intent.putExtra("busPickUpPointLocation", busPickUpPoints[adapter.getSelectedPosition()].location)
                                                        startActivity(intent)
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                            }catch (e: Exception) {
                                Toast.makeText(this@ChoosePickUpLocationActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this@ChoosePickUpLocationActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
        }
    }
}