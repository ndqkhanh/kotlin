package com.example.kotlin

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class BookingDetailActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var paymentDialog: AlertDialog
    private lateinit var btnPay: Button
    private lateinit var rvBookingDetail: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_detail)

        BookingDetailList.bookingDetails = BookingDetailList.generateBookingDetailList(
            "Nguyen Van A",
            "A1, A2, A3",
            "123456, 123457, 123458",
            "Bux",
            "Ha Noi",
            "Ho Chi Minh",
            "12:00",
            "13:00",
            "1 hour",
            "No refund, No change, No cancellation, No reschedulemmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm",
            "3",
            "Normal",
            "10000",
            "30000",
            "Paid"
        )

        rvBookingDetail = findViewById(R.id.rvBookingDetail)
        rvBookingDetail.adapter = CustomBookingDetailAdapter(BookingDetailList.bookingDetails)
        rvBookingDetail.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)

        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        btnPay = findViewById(R.id.btnPay)
        btnPay.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.alert_payment_status_layout, null)
            builder.setView(view)
            builder.setCancelable(false)
            val btnPrintPdf = view.findViewById<Button>(R.id.btnPrintPdf)
            btnPrintPdf.setOnClickListener {
                Toast.makeText(this, "Print PDF", Toast.LENGTH_SHORT).show()
            }
            val btnHome = view.findViewById<Button>(R.id.btnHome)
            btnHome.setOnClickListener {
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
            }
            paymentDialog = builder.create()
            paymentDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            paymentDialog.show()
        }
    }
}