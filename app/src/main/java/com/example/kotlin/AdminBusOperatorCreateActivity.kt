package com.example.kotlin

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import retrofit2.awaitResponse

class AdminBusOperatorCreateActivity:AppCompatActivity() {
    lateinit var busOperatorNameEt: EditText
    lateinit var busOperatorPhoneEt: EditText
    lateinit var busOperatorImageEt: EditText
    lateinit var createBtn: Button
    lateinit var busOperatorData: BusOperatorBody

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_bus_operator_create)
        val retrofit = APIServiceImpl()
        val token = " BEARER eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI3ZmU0YTNlZS0zMjRiLTQ0NWQtODYzYy0wN2ZjNzAyYmQ4NDQiLCJpYXQiOjE2ODA0MjIxMzIsImV4cCI6MTY4MDQyMzkzMiwidHlwZSI6ImFjY2VzcyJ9.VH4RjvIY5v8eVfswbcrAMJjjk1neThzIjPOBUjDfu7M"

        busOperatorNameEt = findViewById(R.id.adminBusOperatorNameCreateEt)
        busOperatorPhoneEt = findViewById(R.id.adminBusOperatorPhoneCreateEt)
        createBtn = findViewById(R.id.adminBusOperatorCreateBtn)



        createBtn.setOnClickListener {
            var busOperatorName = busOperatorNameEt.text.toString()
            var busOperatorPhone = busOperatorPhoneEt.text.toString()
            var busOperatorImage = busOperatorImageEt.text.toString()

            // CREATE DIALOG TO ANNOUNCE
            if (busOperatorName == "" && busOperatorPhone == "" && busOperatorImage == "")
                Toast.makeText(this, "Invalid information. Enter again", Toast.LENGTH_SHORT).show()
            // TODO CREATE BUS OP
            else {

                busOperatorData = BusOperatorBody(busOperatorImage,busOperatorPhone,busOperatorName)

                val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
                    throwable.printStackTrace()

                }


                GlobalScope.launch (Dispatchers.IO + coroutineExceptionHandler) {


                    var response = retrofit.adminCreateBusOperator().createBusOperator(token,busOperatorData).awaitResponse()
                    Log.d("Response", "vui 1" + response.message())
                    // debug response
                    Log.d("Response", response.toString())
                    if(response.isSuccessful){
                        Log.d("Response", "vui 2")
                        val data = response.body()!!
                        Log.d("Response", data.toString())

                    }


                }


                // Send information to list

            }

        }



    }
}