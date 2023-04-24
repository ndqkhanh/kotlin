package com.example.kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.text.HtmlCompat
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
        val busPickUpPointId = intent.getStringExtra("busPickUpPointId")
        val busPickUpPointName = intent.getStringExtra("busPickUpPointName")
        val busPickUpPointLocation = intent.getStringExtra("busPickUpPointLocation")
        val busDropDownPointId = intent.getStringExtra("busDropDownPointId")
        val busDropDownPointName = intent.getStringExtra("busDropDownPointName")
        val busDropDownPointLocation = intent.getStringExtra("busDropDownPointLocation")

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
                            val txtSoLuongGheTrong = findViewById<TextView>(R.id.txtSoLuongGheTrong)

                            val tmp = "Số lượng ghế trống: " + body2.left_seats.toString()
                            txtSoLuongGheTrong.text = tmp

                            val edtPersonName = findViewById<EditText>(R.id.edtPersonName)
                            val edtPhoneNumber = findViewById<EditText>(R.id.edtPhone)
                            val edtNumOfSeats = findViewById<EditText>(R.id.edtNumOfSeats)

                            val continueBtn = findViewById<AppCompatButton>(R.id.continueBtn)
                            continueBtn.setOnClickListener {
                                if(edtPersonName.text.toString().isEmpty() || edtPhoneNumber.text.toString().isEmpty() || edtNumOfSeats.text.toString().isEmpty()
                                    || edtPersonName.text.toString().isBlank() || edtPhoneNumber.text.toString().isBlank() || edtNumOfSeats.text.toString().isBlank()
                                ) {
                                    Toast.makeText(this@EnterInformationActiviy, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                                }
                                // check if phone number contains only numbers
                                else if(!edtPhoneNumber.text.toString().matches(Regex("[0-9]+"))) {
                                    Toast.makeText(this@EnterInformationActiviy, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show()
                                }
                                else if (edtNumOfSeats.text.toString().toInt() <= 0 ){
                                    Toast.makeText(this@EnterInformationActiviy, "Số lượng ghế không hợp lệ", Toast.LENGTH_SHORT).show()
                                } else {
                                    if(edtNumOfSeats.text.toString().toInt() > body2.left_seats) {
                                        Toast.makeText(this@EnterInformationActiviy, "Số lượng ghế không đủ", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val intent = Intent(this@EnterInformationActiviy, ConfirmInformationActivity::class.java)
                                        intent.putExtra("busId", busId)
                                        intent.putExtra("busPickUpPointId", busPickUpPointId)
                                        intent.putExtra("busPickUpPointName", busPickUpPointName)
                                        intent.putExtra("busPickUpPointLocation", busPickUpPointLocation)
                                        intent.putExtra("busDropDownPointName", busDropDownPointName)
                                        intent.putExtra("busDropDownPointLocation", busDropDownPointLocation)
                                        intent.putExtra("busDropDownPointId", busDropDownPointId)
                                        intent.putExtra("personName", edtPersonName.text.toString())
                                        intent.putExtra("phoneNumber", edtPhoneNumber.text.toString())
                                        intent.putExtra("numOfSeats", edtNumOfSeats.text.toString())
                                        startActivity(intent)
                                    }
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