package com.example.kotlin.User.Screen.Booking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.kotlin.APIServiceImpl
import com.example.kotlin.MainActivity
import com.example.kotlin.R
import com.example.kotlin.TicketData
import com.example.kotlin.User.Screen.ChiTietChuyenXe
import com.example.kotlin.utils.UserInformation
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
        val boId = intent.getStringExtra("boId")
        val busPickUpPointId = intent.getStringExtra("busPickUpPointId")
        val busPickUpPointName = intent.getStringExtra("busPickUpPointName")
        val busPickUpPointLocation = intent.getStringExtra("busPickUpPointLocation")
        val busDropDownPointId = intent.getStringExtra("busDropDownPointId")
        val busDropDownPointName = intent.getStringExtra("busDropDownPointName")
        val busDropDownPointLocation = intent.getStringExtra("busDropDownPointLocation")
        val personName = intent.getStringExtra("personName")
        val phoneNumber = intent.getStringExtra("phoneNumber")
        val numOfSeats = intent.getStringExtra("numOfSeats")
        val note = intent.getStringExtra("note")

        val txtBusOperatorDetail = findViewById<AppCompatButton>(R.id.txtBusOperatorDetail)
        txtBusOperatorDetail.setOnClickListener {
            val intent = Intent(this, ChiTietChuyenXe::class.java)
            intent.putExtra("bId", busId)
            intent.putExtra("boId", boId)
            startActivity(intent)
        }

        Log.i("busPickUpPointId", busPickUpPointId.toString())
        Log.i("busDropDownPointId", busDropDownPointId.toString())

        val token = UserInformation.TOKEN

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
                            val nameTmp = body2.bus_operators.name + " bus"
                            txtBusOperatorName.text = nameTmp
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
                            Log.i("UserInformation.USER", UserInformation.USER.toString())
                            txtEmail.text = UserInformation.USER?.email_contact

                            val txtNote = findViewById<TextView>(R.id.txtNote)
                            txtNote.text = if(note == "") "Không có ghi chú" else note

                            val continueBtn = findViewById<AppCompatButton>(R.id.continueBtn)
                            continueBtn.setOnClickListener {
                                try {
                                    GlobalScope.launch(Dispatchers.IO) {
                                        val response =
                                            retrofit.createTicket(token.toString())
                                                .createTicketByNumOfSeats(
                                                    busId,
                                                    TicketData(
                                                        personName!!,
                                                        busPickUpPointId!!,
                                                        busDropDownPointId!!,
                                                        phoneNumber!!,
                                                        numOfSeats.toInt(),
                                                        txtNote.text.toString()
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
                                                    Log.i("id ve nha", body3.data.id)
                                                    val intent = Intent(this@ConfirmInformationActivity, PaymentActivity::class.java)
                                                    intent.putExtra("ticketId", body3.data.id)
                                                    intent.putExtra("busOperatorName", txtBusOperatorName.text.toString())
                                                    intent.putExtra("time", txtTime.text.toString())
                                                    intent.putExtra("totalPrice", totalPrice)
                                                    intent.putExtra("seats", body3.data.seats)
                                                    intent.putExtra("soLuong", txtSoVe.text)
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
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    val intent = Intent(this@ConfirmInformationActivity, MainActivity::class.java)
                                                    startActivity(intent)
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