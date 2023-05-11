package com.example.kotlin.User.Screen.Booking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.kotlin.MainActivity
import com.example.kotlin.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class PaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val busOperatorName = intent.getStringExtra("busOperatorName")
        val time = intent.getStringExtra("time")

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
                startActivity(intent)
            }

            bottomSheetView.findViewById<Button>(R.id.btnPay).setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
    }
}