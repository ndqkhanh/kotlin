package com.example.kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import java.text.DecimalFormat

class ConfirmInformationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_information)

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
        val personName = intent.getStringExtra("personName")
        val phoneNumber = intent.getStringExtra("phoneNumber")
        val numOfSeats = intent.getStringExtra("numOfSeats")

        Log.i("busPickUpPointId", busPickUpPointId.toString())
        Log.i("busDropDownPointId", busDropDownPointId.toString())

//        val token = this.getSharedPreferences("vexere", MODE_PRIVATE).getString("token", "")

        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJjMTE4ZjY5My04NzIyLTQ0NjEtYTc5ZC1kNzY5OTFiOTZiY2QiLCJpYXQiOjE2ODIxNDMzMDUsImV4cCI6MTY4MjE0NTEwNSwidHlwZSI6ImFjY2VzcyJ9.NcSNJN-oklIicwSVk7m1ujOPI1ln7HHH_mYkyrh58WI"


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

                            val txtTuyen = findViewById<TextView>(R.id.txtTuyen)
                            val textTuyen = body2.start_point.name + " - " + body2.end_point.name
                            txtTuyen.text = textTuyen
                            val txtNhaXe = findViewById<TextView>(R.id.txtNhaXe)
                            txtNhaXe.text = body2.bus_operators.name
                            val txtChuyen = findViewById<TextView>(R.id.txtChuyen)
                            txtChuyen.text = body2.start_time
                            val txtLoaiXe = findViewById<TextView>(R.id.txtLoaiXe)
                            txtLoaiXe.text = if (body2.type == 0) "Ghế ngồi"
                            else if (body2.type == 1) "Giường nằm"
                            else "Giường nằm đôi"
                            val txtSoVe = findViewById<TextView>(R.id.txtSoVe)
                            val temp = "$numOfSeats vé"
                            txtSoVe.text = temp
                            val txtTamTinh = findViewById<TextView>(R.id.txtTamTinh)
                            val totalPrice = body2.price * numOfSeats!!.toInt()
                            val useDf = DecimalFormat("###,###,###")
                            val tmp = useDf.format(totalPrice)
                            val tmp2 = tmp.replace(",", ".")
                            val temp2 = tmp2 + "đ"
                            txtTamTinh.text = temp2

                            val txtPickUpName = findViewById<TextView>(R.id.txtPickUpName)
                            txtPickUpName.text = busPickUpPointName
                            val txtPickUpLocation = findViewById<TextView>(R.id.txtPickUpLocation)
                            txtPickUpLocation.text = busPickUpPointLocation
                            val txtPickUpTime = findViewById<TextView>(R.id.txtPickUpTime)
                            val temp3 = "Dự kiến đón lúc " + body2.start_time
                            txtPickUpTime.text = temp3

                            val txtDropDownName = findViewById<TextView>(R.id.txtDropDownName)
                            txtDropDownName.text = busDropDownPointName
                            val txtDropDownLocation = findViewById<TextView>(R.id.txtDropDownLocation)
                            txtDropDownLocation.text = busDropDownPointLocation
                            val txtDropDownTime = findViewById<TextView>(R.id.txtDropDownTime)
                            val temp4 = "Dự kiến trả lúc " + body2.end_time
                            txtDropDownTime.text = temp4

                            val txtHoTen = findViewById<TextView>(R.id.txtHoTen)
                            txtHoTen.text = personName
                            val txtDienThoai = findViewById<TextView>(R.id.txtDienThoai)
                            txtDienThoai.text = phoneNumber
                            val txtEmail = findViewById<TextView>(R.id.txtEmail)
                            txtEmail.text = UserInformation.EMAIL

                            val continueBtn = findViewById<AppCompatButton>(R.id.continueBtn)
                            continueBtn.setOnClickListener {
                                try {
                                    GlobalScope.launch(Dispatchers.IO) {
                                        val response =
                                            retrofit.createTicket(token!!)
                                                .createTicketByNumOfSeats(
                                                    busId,
                                                    TicketData(
                                                        personName!!,
                                                        busPickUpPointId!!,
                                                        busDropDownPointId!!,
                                                        phoneNumber!!,
                                                        numOfSeats.toInt()
                                                    )
                                                ).awaitResponse()
                                        // debug response
                                        Log.d("Response", response.toString())
                                        if (response.isSuccessful) {
                                            val body3 = response.body()
                                            Log.d("Response", body3.toString())
                                            val status = body3?.status
                                            if(status == true){
                                                launch(Dispatchers.Main) {
                                                    Toast.makeText(
                                                        this@ConfirmInformationActivity,
                                                        "Đặt vé thành công",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    val intent = Intent(this@ConfirmInformationActivity, PaymentActivity::class.java)
                                                    val ticketIdsJoin = body3.data.ticket_ids.joinToString(",")
                                                    intent.putExtra("ticketIds", ticketIdsJoin)
                                                    intent.putExtra("totalPrice", totalPrice)
                                                    startActivity(intent)
                                                }
                                            }else
                                                launch(Dispatchers.Main) {
                                                    Toast.makeText(
                                                        this@ConfirmInformationActivity,
                                                        "Đã xảy ra lỗi, xin hãy kiểm tra lại kết nối", Toast.LENGTH_SHORT).show()
                                                }
                                        }else{
                                            launch(Dispatchers.Main) {
                                                if(response.code() == 401){
                                                    Toast.makeText(
                                                        this@ConfirmInformationActivity,
                                                        "Phiên đăng nhập đã hết hạn.\nVui lòng đăng nhập lại.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
//                                                    val intent = Intent(this@ConfirmInformationActivity, LoginActivity::class.java)
//                                                    startActivity(intent)
                                                }
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    launch(Dispatchers.Main) {
                                        Toast.makeText(
                                            this@ConfirmInformationActivity,
                                            "Đã xảy ra lỗi, xin hãy kiểm tra lại kết nối", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                this@ConfirmInformationActivity,
                "Đã xảy ra lỗi, xin hãy kiểm tra lại kết nối", Toast.LENGTH_SHORT).show()
        }
    }
}