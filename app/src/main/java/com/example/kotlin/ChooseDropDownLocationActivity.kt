package com.example.kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class ChooseDropDownLocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_drop_down_location)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val retrofit = APIServiceImpl()

        val busId = intent.getStringExtra("busId")
        val busPickUpPointId = intent.getStringExtra("busPickUpPointId")
        val busPickUpPointName = intent.getStringExtra("busPickUpPointName")
        val busPickUpPointLocation = intent.getStringExtra("busPickUpPointLocation")

        val busDropDownPoints = ArrayList<Point>()

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
                                        retrofit.point().getPointsByBsId(body.end_point.id).awaitResponse()
                                    if (response2.isSuccessful) {
                                        val body2 = response2.body()
                                        launch(Dispatchers.Main) {
                                            if (body2 != null) {
                                                for (i in 0 until body2.data.size) {
                                                    busDropDownPoints.add(Point(body2.data[i].point_id, body.end_time,
                                                        body2.data[i].points.name, body2.data[i].points.location))
                                                }
                                                val listView = findViewById<ListView>(R.id.lvDiemTra)
                                                val adapter = PickUpPointAdapter(this@ChooseDropDownLocationActivity, busDropDownPoints)
                                                listView.adapter = adapter
                                                listView.onItemClickListener =
                                                    AdapterView.OnItemClickListener { _, _, position, _ -> adapter.setSelectedItem(position) }

                                                val continueBtn = findViewById<AppCompatButton>(R.id.continueBtn)
                                                continueBtn.setOnClickListener {
                                                    if(adapter.getSelectedPosition() == -1) {
                                                        Toast.makeText(this@ChooseDropDownLocationActivity, "Hãy chọn điếm trả bạn muốn", Toast.LENGTH_SHORT).show()
                                                    }
                                                    else {
                                                        intent = Intent(this@ChooseDropDownLocationActivity, EnterInformationActiviy::class.java)
                                                        intent.putExtra("busId", busId)
                                                        intent.putExtra("busDropDownPointId", busDropDownPoints[adapter.getSelectedPosition()].id)
                                                        intent.putExtra("busDropDownPointName", busDropDownPoints[adapter.getSelectedPosition()].name)
                                                        intent.putExtra("busDropDownPointLocation", busDropDownPoints[adapter.getSelectedPosition()].location)
                                                        intent.putExtra("busPickUpPointId", busPickUpPointId)
                                                        intent.putExtra("busPickUpPointName", busPickUpPointName)
                                                        intent.putExtra("busPickUpPointLocation", busPickUpPointLocation)
                                                        startActivity(intent)
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                            }catch (e: Exception) {
                                Toast.makeText(this@ChooseDropDownLocationActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this@ChooseDropDownLocationActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
        }
    }
}