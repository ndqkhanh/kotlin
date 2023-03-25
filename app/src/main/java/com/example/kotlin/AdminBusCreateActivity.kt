package com.example.kotlin

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse



class AdminBusCreateActivity: AppCompatActivity() {
    lateinit var addBtn: Button

    // THIS WILL BE REPLACED WHENEVER HOANG LOGIN BY FACEBOOK
    // TOKEN WILL GET FROM THERE
    val token = "Bearer  eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI3ZmU0YTNlZS0zMjRiLTQ0NWQtODYzYy0wN2ZjNzAyYmQ4NDQiLCJpYXQiOjE2Nzk3MzM5MTQsImV4cCI6MTY3OTczNTcxNCwidHlwZSI6ImFjY2VzcyJ9.uQsh3SoCWeCoYe3l5yMUfMhiSbQDGtEjsSZrM2RPTuA"

    // Sample data
    lateinit var sampleData : AdminBusCreateBody


    // TODO Fragment not activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_bus_create)

        // Create sample data
        sampleData = AdminBusCreateBody(
            "fbb09364-3e67-46e2-bf0f-95670d827544",
            "402bdf26-013e-4438-9813-b4d8c326e60d",
            "402bdf26-013e-4438-9813-b4d8c326e60d",
            0,
            "01/03/2022",
            "01/05/2022",
            "https://loremflickr.com/300/200",
            "Hello",
            23,
            53000
        )




        // TODO Integrate

        addBtn = findViewById(R.id.addBtnAdminBusCreateBtn)
        val retrofit = APIServiceImpl()

        addBtn.setOnClickListener {
            val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
                throwable.printStackTrace()
            }
            // THIS LOOK LIKE I CAN CALL API SUCCESSFULLY BUT REPONSE IS NOT HANDLE CORRECTLY
            GlobalScope.launch (Dispatchers.IO + coroutineExceptionHandler) {
                val response = retrofit.adminCreateBus().adminCreateBus(token,sampleData).awaitResponse()
                Log.d("Response", "vui 1" + response.message())
                // debug response
                Log.d("Response", response.toString())
                if(response.isSuccessful){
                    Log.d("Response", "vui 2")
                    val data = response.body()!!
                    Log.d("Response", data.toString())
                }
            }
        }
        }

}