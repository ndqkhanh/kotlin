package com.example.kotlin.User.Screen.Booking

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import com.example.kotlin.APIServiceImpl
import com.example.kotlin.PickUpPointAdapter
import com.example.kotlin.Point
import com.example.kotlin.R
import com.example.kotlin.User.Screen.ChiTietChuyenXe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class ChooseDropDownLocationActivity : AppCompatActivity() {
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_drop_down_location)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val retrofit = APIServiceImpl()

        val busId = intent.getStringExtra("busId")
        val boId = intent.getStringExtra("boId")
        val numOfSeats = intent.getStringExtra("numOfSeats")
        val busPickUpPointId = intent.getStringExtra("busPickUpPointId")
        val busPickUpPointName = intent.getStringExtra("busPickUpPointName")
        val busPickUpPointLocation = intent.getStringExtra("busPickUpPointLocation")

        val txtBusOperatorDetail = findViewById<AppCompatButton>(R.id.txtBusOperatorDetail)
        txtBusOperatorDetail.setOnClickListener {
            val intent = Intent(this, ChiTietChuyenXe::class.java)
            intent.putExtra("bId", busId)
            intent.putExtra("boId", boId)
            startActivity(intent)
        }

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
                                                val continueBtn = findViewById<AppCompatButton>(R.id.continueBtn)
                                                continueBtn.isClickable = false
                                                continueBtn.isEnabled = false
                                                for (i in 0 until body2.data.size) {
                                                    busDropDownPoints.add(
                                                        Point(body2.data[i].point_id, body.end_time,
                                                        body2.data[i].points.name, body2.data[i].points.location)
                                                    )
                                                }
                                                val listView = findViewById<ListView>(R.id.lvDiemTra)
                                                val adapter = PickUpPointAdapter(this@ChooseDropDownLocationActivity, busDropDownPoints)
                                                listView.adapter = adapter
                                                listView.onItemClickListener =
                                                    AdapterView.OnItemClickListener { _, _, position, _ ->
                                                        adapter.setSelectedItem(position)
                                                        continueBtn.isClickable = true
                                                        continueBtn.isEnabled = true
                                                        continueBtn.setBackgroundDrawable(getDrawable(
                                                            R.drawable.yellow_panel
                                                        ))
                                                    }
                                                continueBtn.setOnClickListener {
                                                    if(adapter.getSelectedPosition() == -1) {
                                                        Toast.makeText(this@ChooseDropDownLocationActivity, "Hãy chọn điếm trả bạn muốn", Toast.LENGTH_SHORT).show()
                                                    }
                                                    else {
                                                        intent = Intent(this@ChooseDropDownLocationActivity, EnterInformationActiviy::class.java)
                                                        intent.putExtra("busId", busId)
                                                        intent.putExtra("numOfSeats", numOfSeats)
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
                                Toast.makeText(this@ChooseDropDownLocationActivity, "Đã xảy ra lỗi, xin hãy kiểm tra lại kết nối", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this@ChooseDropDownLocationActivity, "Đã xảy ra lỗi, xin hãy kiểm tra lại kết nối", Toast.LENGTH_SHORT).show()
        }
    }
}