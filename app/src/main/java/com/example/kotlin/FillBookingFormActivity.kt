package com.example.kotlin

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class FillBookingFormActivity : AppCompatActivity() {
    private lateinit var btnBook: Button
    private lateinit var edtPhone: EditText
    private lateinit var edtNumOfSeats: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fill_booking_form)

        edtPhone = findViewById(R.id.edtPhone)
        edtNumOfSeats = findViewById(R.id.edtNumOfSeats)

        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJjMTE4ZjY5My04NzIyLTQ0NjEtYTc5ZC1kNzY5OTFiOTZiY2QiLCJpYXQiOjE2Nzk3NDI3MjgsImV4cCI6MTY3OTc0NDUyOCwidHlwZSI6ImFjY2VzcyJ9.j8l_RwDGBVEpGZP761DLFeqKm_ph09ow4Iar5L1dKHI"
        val busId = "384fdcb1-496f-4f87-8b1e-578674111ac1"

        btnBook = findViewById(R.id.btnBook)
        btnBook.setOnClickListener {
            var flag = false

            if (!edtPhone.text.contains(Regex("\\d+"))) {
                edtPhone.error = "Phone number is invalid"
                flag = true
            }
            if (!edtNumOfSeats.text.contains(Regex("\\d+"))) {
                edtNumOfSeats.error = "Number of seats is invalid"
                flag = true
            }

            if (flag) return@setOnClickListener
            val dialog = AlertDialog.Builder(this)
            dialog.apply {
                setTitle("Booking Confirmation")
                setMessage("Are you sure to book this bus?")
                setPositiveButton("Yes") { _, _ ->
                    val retrofit = APIServiceImpl()

                    GlobalScope.launch(Dispatchers.IO) {
                        val response =
                            retrofit.createTicket(token)
                                .createTicketByNumOfSeats(
                                    busId,
                                    TicketData(
                                        edtPhone.text.toString(),
                                        edtNumOfSeats.text.toString().toInt()
                                    )
                                ).awaitResponse()
                        // debug response
                        Log.d("Response", response.toString())
                        if (response.isSuccessful) {
                            val body = response.body()
                            Log.d("Response", body.toString())
                            val error = body?.error
                            if (error == null) {
                                val intent =
                                    Intent(
                                        this@FillBookingFormActivity,
                                        BookingDetailActivity::class.java
                                    )
                                intent.putIntegerArrayListExtra("seat_positions",
                                    body?.seat_positions?.let { it1 -> ArrayList(it1) })
                                intent.putStringArrayListExtra("ticket_ids",
                                    body?.ticket_ids?.let { it1 -> ArrayList(it1) })
                                intent.putExtra("name", body?.name)
                                intent.putExtra("email", body?.email)
                                intent.putExtra("phone", body?.phone)
                                intent.putExtra("bo_name", body?.bo_name)
                                intent.putExtra("start_point", body?.start_point)
                                intent.putExtra("end_point", body?.end_point)
                                intent.putExtra("start_time", body?.start_time)
                                intent.putExtra("end_time", body?.end_time)
                                intent.putExtra("duration", body?.duration)
                                intent.putExtra("num_of_seats", body?.num_of_seats)
                                intent.putExtra("type", body?.type)
                                intent.putExtra("ticket_cost", body?.ticket_cost)
                                intent.putExtra("total_cost", body?.total_cost)
                                intent.putExtra("status", body?.status)

                                startActivity(intent)
                            } else {
                                launch(Dispatchers.Main) {
                                    val errorDialog =
                                        AlertDialog.Builder(this@FillBookingFormActivity)
                                    errorDialog.apply {
                                        setTitle("Error")
                                        setMessage(error)
                                        setPositiveButton("OK") { dialogInterface: DialogInterface, _ ->
                                            dialogInterface.dismiss()
                                        }
                                    }
                                    errorDialog.show()
                                }
                            }
                        } else {
                            launch(Dispatchers.Main) {
                                val message = response.message()
                                val errorDialog =
                                    AlertDialog.Builder(this@FillBookingFormActivity)
                                errorDialog.apply {
                                    setTitle("Error")
                                    setMessage(message)
                                    setPositiveButton("OK") { dialogInterface: DialogInterface, _ ->
                                        dialogInterface.dismiss()
                                    }
                                }
                                errorDialog.show()
                            }
                        }
                    }
                }
                setNegativeButton("No") { dialogInterface: DialogInterface, _ ->
                    dialogInterface.dismiss()
                }
            }
            dialog.show()
        }
    }
}