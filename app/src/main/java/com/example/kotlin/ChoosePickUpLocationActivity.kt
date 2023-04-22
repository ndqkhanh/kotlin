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

        val busId = "b655665e-b14e-4a87-b45c-ff019df079b9";

        val busPickUpPoints = ArrayList<Point>()

        try {
            GlobalScope.launch(Dispatchers.IO) {
                val response =
                    retrofit.getAllBusStations().getBusStations().awaitResponse()
                val response2 =
                    retrofit.bus().getBusById(busId).awaitResponse()
                if (response.isSuccessful && response2.isSuccessful){
                    val body = response.body()
                    val body2 = response2.body()
                    Log.i("body", body.toString())
                    Log.i("body2", body2.toString())
                    launch(Dispatchers.Main) {
                        if (body != null && body2 != null) {
                            for (i in 0 until body.data.size) {
                                busPickUpPoints.add(Point(body.data[i].id, body2.start_time,
                                    body.data[i].name, body.data[i].location))
                            }
                            val txtBusOperatorName = findViewById<TextView>(R.id.txtBusOperatorName)
                            txtBusOperatorName.text = body2.bus_operators.name
                            val txtTime = findViewById<TextView>(R.id.txtTime)
                            txtTime.text = body2.start_time
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}