package com.example.kotlin

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class BookingDetailActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var paymentDialog: AlertDialog
    private lateinit var btnPay: Button
    private lateinit var rvBookingDetail: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_detail)

        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJjMTE4ZjY5My04NzIyLTQ0NjEtYTc5ZC1kNzY5OTFiOTZiY2QiLCJpYXQiOjE2Nzk3NDI3MjgsImV4cCI6MTY3OTc0NDUyOCwidHlwZSI6ImFjY2VzcyJ9.j8l_RwDGBVEpGZP761DLFeqKm_ph09ow4Iar5L1dKHI"

        val name = intent.getStringExtra("name")
        val email = intent.getStringExtra("email")
        val phone = intent.getStringExtra("phone")
        val seatPositions = intent.getIntegerArrayListExtra("seat_positions")
        val ticketIds = intent.getStringArrayListExtra("ticket_ids")
        val boName = intent.getStringExtra("bo_name")
        val startPoint = intent.getStringExtra("start_point")
        val endPoint = intent.getStringExtra("end_point")
        val startTime = intent.getStringExtra("start_time")
        val endTime = intent.getStringExtra("end_time")
        val duration = intent.getStringExtra("duration")
        val numOfSeats = intent.getStringExtra("num_of_seats")
        val busType = intent.getStringExtra("type")
        val ticketCost = intent.getStringExtra("ticket_cost")
        val totalCost = intent.getStringExtra("total_cost")
        val status = intent.getStringExtra("status")

        Log.d("seatPositions", seatPositions.toString())
        Log.d("ticketIds", ticketIds.toString())

        BookingDetailList.bookingDetails = BookingDetailList.generateBookingDetailList(
            name ?: "",
            email ?: "",
            seatPositions ?: ArrayList(),
            ticketIds ?: ArrayList(),
            phone ?: "",
            boName ?: "",
            startPoint ?: "",
            endPoint ?: "",
            startTime ?: "",
            endTime ?: "",
            duration ?: "",
            numOfSeats ?: "",
            busType ?: "",
            ticketCost ?: "",
            totalCost ?: "",
            status ?: ""
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
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Payment")
            dialog.setMessage("Are you sure to pay for this ticket?")
            dialog.setPositiveButton("Yes") { _, _ ->
                val retrofit = APIServiceImpl()

                GlobalScope.launch(Dispatchers.IO) {
                    val response =
                        retrofit.createPayment(token)
                            .createPaymentByTicketIds(
                                TicketPaymentData(
                                    ticketIds ?: ArrayList()
                                )
                            ).awaitResponse()
                    // debug response
                    Log.d("Response", response.toString())
                    if (response.isSuccessful) {
                        val body = response.body()
                        Log.d("Response", body?.message.toString())
                        launch(Dispatchers.Main) {
                            val builder = AlertDialog.Builder(this@BookingDetailActivity)
                            val view =
                                layoutInflater.inflate(R.layout.alert_payment_status_layout, null)
                            builder.setView(view)
                            builder.setCancelable(false)
                            val btnPrintPdf = view.findViewById<Button>(R.id.btnPrintPdf)
                            btnPrintPdf.setOnClickListener {
                                // TODO
                                Toast.makeText(
                                    this@BookingDetailActivity,
                                    "Print PDF",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                            val btnHome = view.findViewById<Button>(R.id.btnHome)
                            btnHome.setOnClickListener {
                                Intent(this@BookingDetailActivity, Home::class.java)
                                    .also {
                                        startActivity(it)
                                    }
                            }
                            paymentDialog = builder.create()
                            paymentDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                            paymentDialog.show()
                        }
                    }
                }
            }
            dialog.setNegativeButton("No") { dialogInterface: DialogInterface, _ ->
                dialogInterface.dismiss()
            }
            dialog.show()
        }
    }
}