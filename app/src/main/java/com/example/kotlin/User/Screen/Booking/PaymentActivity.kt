package com.example.kotlin.User.Screen.Booking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.kotlin.APIServiceImpl
import com.example.kotlin.DataClass.HistoryItem
import com.example.kotlin.DataClass.SuccessMessage
import com.example.kotlin.MainActivity
import com.example.kotlin.R
import com.example.kotlin.utils.UserInformation
import com.example.kotlin.utils.ZaloPay.CreateOrder
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener
import java.text.DecimalFormat

class PaymentActivity : AppCompatActivity() {
    lateinit var totalTv: TextView
    lateinit var numsOfTicketTV: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val busOperatorName = intent.getStringExtra("busOperatorName")
        val time = intent.getStringExtra("time")
        val id = intent.getStringExtra("ticketId")
        val price = intent.getIntExtra("totalPrice", 0 )
        val numsOfTicket = intent.getStringExtra("soLuong")

        val txtBusOperatorName = findViewById<TextView>(R.id.txtBusOperatorName)
        val txtTime = findViewById<TextView>(R.id.txtTime)

        txtBusOperatorName.text = busOperatorName
        txtTime.text = time

        val btnBack:ImageButton = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(
                this, com.google.android.material.R.style.Theme_Design_BottomSheetDialog
            )
            val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
                R.layout.layout_payment_bottom_sheet,
                findViewById<ConstraintLayout>(R.id.bottomSheet)
            )

            bottomSheetView.findViewById<Button>(R.id.btnBack).setOnClickListener {

                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                finishAffinity()
                startActivity(intent)
            }

            bottomSheetView.findViewById<Button>(R.id.btnPay).setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }

        val useDf = DecimalFormat("###,###,###")
        totalTv = findViewById(R.id.costVal)
        totalTv.text = useDf.format(price).replace(",",".") + "đ"

        numsOfTicketTV = findViewById(R.id.numsOfTickets)
        val ticketCount = numsOfTicket!!.split(" ").get(0).toInt()

        numsOfTicketTV.text =  useDf.format(price / ticketCount).replace(",",".") + " x " + ticketCount.toString()

        findViewById<Button>(R.id.paymentBtn).setOnClickListener {
            if(id != null) {
                zp(id!!, price.toString())
            }
        }
    }

    private fun zp(id: String, price: String){
        val orderApi = CreateOrder()

        val data = orderApi.createOrder(price)
        val code = data.getString("return_code")

        if (code == "1") {

            val token = data.getString("zp_trans_token")
            ZaloPaySDK.getInstance().payOrder(this, token, "demozpdk://app", object :
                PayOrderListener {
                override fun onPaymentCanceled(zpTransToken: String, appTransID: String) {
                    //Handle User Canceled
                    Log.i("abcd","can")
                }

                override fun onPaymentError(
                    zaloPayErrorCode: ZaloPayError,
                    zpTransToken: String,
                    appTransID: String
                ) {
                    //Redirect to Zalo/ZaloPay Store when zaloPayError == ZaloPayError.PAYMENT_APP_NOT_FOUND
                    //Handle Error
                }

                override fun onPaymentSucceeded(
                    transactionId: String,
                    transToken: String,
                    appTransID: String
                ) {
                    //Handle Success

                }
            })
        }
        val callPay = APIServiceImpl.ticketService().payTicket(id, "Bearer ${UserInformation.TOKEN!!}")
        callPay.enqueue(object : Callback<SuccessMessage> {
            override fun onResponse(
                call: Call<SuccessMessage>,
                response: Response<SuccessMessage>
            ) {
            }

            override fun onFailure(call: Call<SuccessMessage>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })

    }
}