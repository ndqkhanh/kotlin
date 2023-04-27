package com.example.kotlin

import android.annotation.SuppressLint
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

class ChoosePickUpLocationActivity : AppCompatActivity() {
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_pick_up_location)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val retrofit = APIServiceImpl()

        val busId = intent.getStringExtra("busId")
        val numOfSeats = intent.getStringExtra("numOfSeats")

        val busPickUpPoints = ArrayList<Point>()

        try {
            GlobalScope.launch(Dispatchers.IO) {
                val response =
                    retrofit.bus().getBusById(busId!!).awaitResponse()
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
                                                val continueBtn = findViewById<AppCompatButton>(R.id.continueBtn)
                                                continueBtn.isClickable = false
                                                continueBtn.isEnabled = false
                                                for (i in 0 until body2.data.size) {
                                                    busPickUpPoints.add(Point(body2.data[i].point_id, body.start_time,
                                                        body2.data[i].points.name, body2.data[i].points.location))
                                                }
                                                val listView = findViewById<ListView>(R.id.lvDiemDon)
                                                val adapter = PickUpPointAdapter(this@ChoosePickUpLocationActivity, busPickUpPoints)
                                                listView.adapter = adapter
                                                listView.onItemClickListener =
                                                    AdapterView.OnItemClickListener { _, _, position, _ ->
                                                        adapter.setSelectedItem(position)
                                                        continueBtn.isClickable = true
                                                        continueBtn.isEnabled = true
                                                        continueBtn.setBackgroundDrawable(getDrawable(R.drawable.yellow_panel))
                                                    }


                                                continueBtn.setOnClickListener {
                                                    if(adapter.getSelectedPosition() == -1) {
                                                        Toast.makeText(this@ChoosePickUpLocationActivity, "Hãy chọn điếm đón bạn muốn", Toast.LENGTH_SHORT).show()
                                                    }
                                                    else {
                                                        intent = Intent(this@ChoosePickUpLocationActivity, ChooseDropDownLocationActivity::class.java)
                                                        intent.putExtra("busId", busId)
                                                        intent.putExtra("numOfSeats", numOfSeats)
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
                                Toast.makeText(this@ChoosePickUpLocationActivity, "Đã xảy ra lỗi, xin hãy kiểm tra lại kết nối", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this@ChoosePickUpLocationActivity, "Đã xảy ra lỗi, xin hãy kiểm tra lại kết nối", Toast.LENGTH_SHORT).show()
        }
    }
}