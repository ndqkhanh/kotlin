package com.example.kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class BookingDetailActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var paymentDialog: AlertDialog
    private lateinit var btnPay: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_detail)

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