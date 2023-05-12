package com.example.kotlin

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.example.kotlin.User.Screen.Booking.ChoosePickUpLocationActivity
import com.example.kotlin.User.Screen.ChiTietChuyenXe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import java.text.DecimalFormat

class Point(val id: String, val time: String, val name: String, val location: String)

class ChooseSeatActivity : AppCompatActivity() {
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_seat)

        val busId = intent.getStringExtra("busId")
        val boId = intent.getStringExtra("boId")

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val txtBusOperatorDetail = findViewById<AppCompatButton>(R.id.txtBusOperatorDetail)
        txtBusOperatorDetail.setOnClickListener {
            val intent = Intent(this, ChiTietChuyenXe::class.java)
            intent.putExtra("bId", busId)
            intent.putExtra("boId", boId)
            Log.i("bId uni inside", busId.toString())
            Log.i("boId uni inside", boId.toString())
            startActivity(intent)
        }

        val retrofit = APIServiceImpl()

        try {
            GlobalScope.launch(Dispatchers.IO) {
                val response =
                    retrofit.bus().getBusById(busId!!).awaitResponse()
                if (response.isSuccessful){
                    val body2 = response.body()
                    Log.i("body", body2.toString())
                    launch(Dispatchers.Main) {
                        if (body2 != null) {
                            val txtBusOperatorName = findViewById<TextView>(R.id.txtBusOperatorName)
                            txtBusOperatorName.text = body2.bus_operators.name
                            val txtTime = findViewById<TextView>(R.id.txtTime)
                            txtTime.text = body2.start_time

                            val useDf = DecimalFormat("###,###,###")
                            val txtType = findViewById<TextView>(R.id.txtType)
                            txtType.text = if (body2.type == 0) "Ghế ngồi"
                            else if (body2.type == 1) "Giường nằm"
                            else "Giường nằm đôi"

                            val txtPricePerSeat = findViewById<TextView>(R.id.txtPricePerSeat)
                            val temp = "${useDf.format(body2.price)}đ/ghế"
                            txtPricePerSeat.text = temp

                            val continueBtn = findViewById<AppCompatButton>(R.id.continueBtn)
                            continueBtn.isClickable = false
                            continueBtn.isEnabled = false

                            val maxSeats = body2.left_seats
                            val btnPlus = findViewById<ImageButton>(R.id.imgPlus)
                            val btnMinus = findViewById<ImageButton>(R.id.imgMinus)
                            val txtNumOfSeats = findViewById<TextView>(R.id.txtNumOfSeats)

                            val txtDaChon = findViewById<TextView>(R.id.txtDaChon)
                            val txtTotalPrice = findViewById<TextView>(R.id.txtTotalPrice)
                            btnPlus.setOnClickListener {
                                if (txtNumOfSeats.text.toString().toInt() < maxSeats) {
                                    var temp1 = txtNumOfSeats.text.toString().toInt()
                                    temp1++
                                    txtNumOfSeats.text = temp1.toString()
                                    val temp2 = "Đã chọn $temp1 ghế"
                                    txtDaChon.text = temp2
                                    val temp3 = "${useDf.format(temp1 * body2.price)}đ"
                                    txtTotalPrice.text = temp3
                                    continueBtn.isClickable = true
                                    continueBtn.isEnabled = true
                                    continueBtn.setBackgroundDrawable(getDrawable(R.drawable.yellow_panel))
                                }else if (txtNumOfSeats.text.toString().toInt() == maxSeats){
                                    // create custom dialog with layout alert_choose_seat_modal
                                    // get button with id btnBack
                                    val build = AlertDialog.Builder(this@ChooseSeatActivity)
                                    val dialogView = layoutInflater.inflate(R.layout.alert_choose_seat_modal, null)
                                    val txtTitle = dialogView.findViewById<TextView>(R.id.txtTitle)
                                    val tm = "Bạn đã chọn hết $maxSeats ghế còn lại"
                                    txtTitle.text = tm
                                    build.setView(dialogView)
                                    val dialog = build.create()
                                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                                    dialog.show()
                                    val btnDaHieu = dialogView.findViewById<Button>(R.id.btnBack)
                                    btnDaHieu.setOnClickListener {
                                        dialog.dismiss()
                                    }
                                }
                            }
                            btnMinus.setOnClickListener {
                                if (txtNumOfSeats.text.toString().toInt() > 1) {
                                    var temp1 = txtNumOfSeats.text.toString().toInt()
                                    temp1--
                                    txtNumOfSeats.text = temp1.toString()
                                    val temp2 = "Đã chọn $temp1 ghế"
                                    txtDaChon.text = temp2
                                    val temp3 = "${useDf.format(temp1 * body2.price)}đ"
                                    txtTotalPrice.text = temp3
                                    continueBtn.isClickable = true
                                    continueBtn.isEnabled = true
                                    continueBtn.setBackgroundDrawable(getDrawable(R.drawable.yellow_panel))
                                }else if (txtNumOfSeats.text.toString().toInt() == 1){
                                    var temp1 = txtNumOfSeats.text.toString().toInt()
                                    temp1--
                                    txtNumOfSeats.text = temp1.toString()
                                    val temp2 = "Đã chọn $temp1 ghế"
                                    txtDaChon.text = temp2
                                    val temp3 = "${useDf.format(temp1 * body2.price)}đ"
                                    txtTotalPrice.text = temp3
                                    continueBtn.isClickable = false
                                    continueBtn.isEnabled = false
                                    continueBtn.setBackgroundDrawable(getDrawable(R.drawable.grey_panel))
                                }
                            }

                            continueBtn.setOnClickListener {
                                val intent = Intent(this@ChooseSeatActivity, ChoosePickUpLocationActivity::class.java)
                                intent.putExtra("busId", busId)
                                intent.putExtra("boId", boId)
                                intent.putExtra("numOfSeats", txtNumOfSeats.text.toString())
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this@ChooseSeatActivity, "Đã xảy ra lỗi, xin hãy kiểm tra lại kết nối", Toast.LENGTH_SHORT).show()
        }
    }
}