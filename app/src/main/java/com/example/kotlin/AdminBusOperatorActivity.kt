package com.example.kotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import retrofit2.awaitResponse

class AdminBusOperatorActivity:AppCompatActivity() {
    lateinit var busOperatorRV: RecyclerView
    lateinit var busOperators: MutableList<BusOperator>

    lateinit var addBusOperatorBtn: Button
    var busOperatorAdapter: AdminBusOperatorAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_bus_operator)

        val retrofit = APIServiceImpl()
        busOperators = mutableListOf()

        val token = " BEARER eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI3ZmU0YTNlZS0zMjRiLTQ0NWQtODYzYy0wN2ZjNzAyYmQ4NDQiLCJpYXQiOjE2ODA0MTc5NDUsImV4cCI6MTY4MDQxOTc0NSwidHlwZSI6ImFjY2VzcyJ9.1j3ruzs6SLA7v-q3IkQBxonAB_1Sr8MwhYmnfjbsBXk"
        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }


        GlobalScope.launch (Dispatchers.IO + coroutineExceptionHandler) {

            var response = retrofit.getAllBusOperators().getBusOperators().awaitResponse()
            Log.d("Response", "vui 1" + response.message())
            // debug response
            Log.d("Response", response.toString())
            if(response.isSuccessful){
                Log.d("Response", "vui 2")
                val data = response.body()!!.data
                Log.d("Response", data.toString())
                for (it in data) busOperators.add(it)

                Log.d("busTickets vui 1: ", busOperators.size.toString())

                withContext(Dispatchers.Main){
                    val space = 15
                    val itemDecoration = SpaceItemDecoration(space)

                    busOperatorAdapter = AdminBusOperatorAdapter(busOperators)
                    busOperatorRV = findViewById(R.id.adminBusOperatorRV)
                    busOperatorRV.adapter = busOperatorAdapter
                    busOperatorRV.layoutManager = LinearLayoutManager(this@AdminBusOperatorActivity,
                        LinearLayoutManager.VERTICAL,false)

                    busOperatorRV.addItemDecoration(itemDecoration)

                }

            }

            // TODO DELETE 1 BOOKING
            busOperatorAdapter?.onButtonClick = {busOperator ->
                GlobalScope.launch (Dispatchers.IO) {
                    Log.d("Button clicked" , busOperator.id)

                    val result = retrofit.adminDeleteBooking().deleteBooking(token, busOperator.id).awaitResponse()

                    withContext(Dispatchers.Main){
                        val pos = busOperators.indexOf(busOperator)
                        busOperatorAdapter?.notifyItemRemoved(pos)
                    }
                }


            }

        }

        addBusOperatorBtn = findViewById(R.id.adminBusOperatorAddBtn)

        val intent = Intent(this, AdminBusOperatorCreateActivity::class.java)
        addBusOperatorBtn.setOnClickListener {
                startActivity(intent)
            }
        }
    }
