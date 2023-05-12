package com.example.kotlin.User.Screen.Booking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import com.example.kotlin.APIServiceImpl
import com.example.kotlin.R
import androidx.core.text.HtmlCompat
import com.example.kotlin.User.Screen.ChiTietChuyenXe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class EnterInformationActiviy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_information_activiy)

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
        val busDropDownPointId = intent.getStringExtra("busDropDownPointId")
        val busDropDownPointName = intent.getStringExtra("busDropDownPointName")
        val busDropDownPointLocation = intent.getStringExtra("busDropDownPointLocation")

        val txtBusOperatorDetail = findViewById<AppCompatButton>(R.id.txtBusOperatorDetail)
        txtBusOperatorDetail.setOnClickListener {
            val intent = Intent(this, ChiTietChuyenXe::class.java)
            intent.putExtra("bId", busId)
            intent.putExtra("boId", boId)
            startActivity(intent)
        }

        try {
            GlobalScope.launch(Dispatchers.IO) {
                val response2 =
                    retrofit.bus().getBusById(busId!!).awaitResponse()
                if (response2.isSuccessful){
                    val body2 = response2.body()
                    Log.i("body2", body2.toString())
                    launch(Dispatchers.Main) {
                        if (body2 != null) {
                            val txtBusOperatorName = findViewById<TextView>(R.id.txtBusOperatorName)
                            txtBusOperatorName.text = body2.bus_operators.name
                            val txtTime = findViewById<TextView>(R.id.txtTime)
                            txtTime.text = body2.start_time

                            val edtPersonName = findViewById<com.google.android.material.textfield.TextInputEditText>(
                                R.id.edtPersonName
                            )
                            val edtPhoneNumber = findViewById<com.google.android.material.textfield.TextInputEditText>(
                                R.id.edtPhone
                            )
                            val edtNote = findViewById<com.google.android.material.textfield.TextInputEditText>(
                                R.id.edtNote
                            )

                            val continueBtn = findViewById<AppCompatButton>(R.id.continueBtn)
                            continueBtn.setOnClickListener {
                                var flag = false
                                if(edtPersonName.text.toString().isBlank()) {
                                    edtPersonName.error = "Vui lòng nhập đầy đủ họ tên"
                                    flag = true
                                }
                                if(edtPhoneNumber.text.toString().isBlank()){
                                    edtPhoneNumber.error = "Vui lòng nhập đầy đủ số điện thoại"
                                    flag = true
                                }else if(!edtPhoneNumber.text.toString().matches(Regex("[0-9]+"))) {
                                    edtPhoneNumber.error = "Số điện thoại không hợp lệ"
                                    flag = true
                                }
                                if(flag) return@setOnClickListener
                                val intent = Intent(this@EnterInformationActiviy, ConfirmInformationActivity::class.java)
                                intent.putExtra("busId", busId)
                                intent.putExtra("boId", boId)
                                intent.putExtra("busPickUpPointId", busPickUpPointId)
                                intent.putExtra("busPickUpPointName", busPickUpPointName)
                                intent.putExtra("busPickUpPointLocation", busPickUpPointLocation)
                                intent.putExtra("busDropDownPointName", busDropDownPointName)
                                intent.putExtra("busDropDownPointLocation", busDropDownPointLocation)
                                intent.putExtra("busDropDownPointId", busDropDownPointId)
                                intent.putExtra("personName", edtPersonName.text.toString())
                                intent.putExtra("phoneNumber", edtPhoneNumber.text.toString())
                                intent.putExtra("note", edtNote.text.toString())
                                intent.putExtra("numOfSeats", numOfSeats)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Đã xảy ra lỗi, xin hãy kiểm tra lại kết nối", Toast.LENGTH_SHORT).show()
        }
    }
}