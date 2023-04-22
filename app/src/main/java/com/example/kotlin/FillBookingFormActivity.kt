//package com.example.kotlin
//
//import android.content.DialogInterface
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageButton
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import retrofit2.awaitResponse
//
//class FillBookingFormActivity : AppCompatActivity() {
//    private lateinit var busId: String
//    private lateinit var edtName: EditText
//    private lateinit var edtEmail: EditText
//    private lateinit var edtStartTime: EditText
//    private lateinit var edtEndTime: EditText
//    private lateinit var edtDestination: EditText
//    private lateinit var btnBook: Button
//    private lateinit var edtPhone: EditText
//    private lateinit var edtNumOfSeats: EditText
//    private lateinit var btnBack: ImageButton
//    private lateinit var btnCancel: Button
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_fill_booking_form)
//
//        busId = intent.getStringExtra("busId").toString()
//
//        edtName = findViewById(R.id.edtName)
//        edtEmail = findViewById(R.id.edtEmail)
//        edtStartTime = findViewById(R.id.edtStartTime)
//        edtEndTime = findViewById(R.id.edtEndTime)
//        edtDestination = findViewById(R.id.edtDestination)
//        edtPhone = findViewById(R.id.edtPhone)
//        edtNumOfSeats = findViewById(R.id.edtNumOfSeats)
//        btnBack = findViewById(R.id.btnBack)
//        btnBack.setOnClickListener {
//            finish()
//        }
//        btnCancel = findViewById(R.id.btnCancel)
//        btnCancel.setOnClickListener {
//            val dialog = AlertDialog.Builder(this)
//            dialog.apply {
//                setTitle("Cancel Booking")
//                setMessage("Are you sure to cancel this booking?")
//                setPositiveButton("Yes") { _, _ ->
//                    finish()
//                }
//                setNegativeButton("No") { _, _ -> }
//            }
//            dialog.create().show()
//        }
//
//
//    }
//
//    override fun onResume() {
//        super.onResume()
//        val retrofit = APIServiceImpl()
//        val token = this.getSharedPreferences("vexere", MODE_PRIVATE).getString("token", "")
//
//        edtName.setText(FBInfor.NAME)
//        edtEmail.setText(FBInfor.EMAIL)
//
//        try {
//            GlobalScope.launch(Dispatchers.IO) {
//                val response =
//                    retrofit.bus().getBusById(busId!!).awaitResponse()
//                // debug response
//                Log.d("Response", response.toString())
//                if (response.isSuccessful) {
//                    val body = response.body()
//                    Log.d("Response", body.toString())
//                    launch(Dispatchers.Main) {
//                        edtStartTime.setText(body?.start_time)
//                        edtEndTime.setText(body?.end_time)
//                        edtDestination.setText(body?.start_point?.name + " - " + body?.end_point?.name)
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        btnBook = findViewById(R.id.btnBook)
//        btnBook.setOnClickListener {
//            var flag = false
//
//            if (!edtPhone.text.contains(Regex("\\d+"))) {
//                edtPhone.error = "Phone number is invalid"
//                flag = true
//            }
//            if (!edtNumOfSeats.text.contains(Regex("\\d+"))) {
//                edtNumOfSeats.error = "Number of seats is invalid"
//                flag = true
//            }
//
//            if (flag) return@setOnClickListener
//            val dialog = AlertDialog.Builder(this)
//            dialog.apply {
//                setTitle("Booking Confirmation")
//                setMessage("Are you sure to book this bus?")
//                setPositiveButton("Yes") { _, _ ->
//                    try {
//                        GlobalScope.launch(Dispatchers.IO) {
//                            val response =
//                                retrofit.createTicket(token!!)
//                                    .createTicketByNumOfSeats(
//                                        busId!!,
//                                        TicketData(
//                                            edtPhone.text.toString(),
//                                            edtNumOfSeats.text.toString().toInt()
//                                        )
//                                    ).awaitResponse()
//                            // debug response
//                            Log.d("Response", response.toString())
//                            if (response.isSuccessful) {
//                                val body = response.body()
//                                Log.d("Response", body.toString())
//                                val error = body?.error
//                                if (error == null) {
//                                    val intent =
//                                        Intent(
//                                            this@FillBookingFormActivity,
//                                            BookingDetailActivity::class.java
//                                        )
//                                    intent.putIntegerArrayListExtra("seat_positions",
//                                        body?.seat_positions?.let { it1 -> ArrayList(it1) })
//                                    intent.putStringArrayListExtra("ticket_ids",
//                                        body?.ticket_ids?.let { it1 -> ArrayList(it1) })
//                                    intent.putExtra("name", FBInfor.NAME)
//                                    intent.putExtra("email", FBInfor.EMAIL)
//                                    intent.putExtra("phone", body?.phone)
//                                    intent.putExtra("bo_name", body?.bo_name)
//                                    intent.putExtra("start_point", body?.start_point)
//                                    intent.putExtra("end_point", body?.end_point)
//                                    intent.putExtra("start_time", body?.start_time)
//                                    intent.putExtra("end_time", body?.end_time)
//                                    intent.putExtra("duration", body?.duration)
//                                    intent.putExtra("num_of_seats", body?.num_of_seats)
//                                    intent.putExtra("type", body?.type)
//                                    intent.putExtra("ticket_cost", body?.ticket_cost)
//                                    intent.putExtra("total_cost", body?.total_cost)
//                                    intent.putExtra("status", body?.status)
//
//                                    startActivity(intent)
//                                } else {
//                                    launch(Dispatchers.Main) {
//                                        val errorDialog =
//                                            AlertDialog.Builder(this@FillBookingFormActivity)
//                                        errorDialog.apply {
//                                            setTitle("Error")
//                                            setMessage(error)
//                                            setPositiveButton("OK") { dialogInterface: DialogInterface, _ ->
//                                                dialogInterface.dismiss()
//                                            }
//                                        }
//                                        errorDialog.show()
//                                    }
//                                }
//                            } else {
//                                launch(Dispatchers.Main) {
//                                    val message = response.message()
//                                    val errorDialog =
//                                        AlertDialog.Builder(this@FillBookingFormActivity)
//                                    errorDialog.apply {
//                                        setTitle("Error")
//                                        setMessage(message)
//                                        setPositiveButton("OK") { dialogInterface: DialogInterface, _ ->
//                                            dialogInterface.dismiss()
//                                        }
//                                    }
//                                    errorDialog.show()
//                                }
//                            }
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//                setNegativeButton("No") { dialogInterface: DialogInterface, _ ->
//                    dialogInterface.dismiss()
//                }
//            }
//            dialog.show()
//        }
//    }
//}