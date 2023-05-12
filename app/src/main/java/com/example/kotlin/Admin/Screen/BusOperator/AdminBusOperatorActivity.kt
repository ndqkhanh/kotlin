package com.example.kotlin.Admin.Screen.BusOperator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin.*
import com.example.kotlin.utils.UserInformation
import kotlinx.coroutines.*
import retrofit2.awaitResponse

class AdminBusOperatorActivity:AppCompatActivity() {
    lateinit var busOperatorRV: RecyclerView
    lateinit var busOperators: MutableList<BusOperator>
    lateinit var backBtn: ImageButton
    lateinit var addBusOperatorBtn: ImageButton
    private lateinit var autoNhaXe: AutoCompleteTextView
    var busOperatorAdapter: AdminBusOperatorAdapter? = null
    val REQUEST_CODE = 1111
    val retrofit = APIServiceImpl()
    val token = "BEARER " + UserInformation.TOKEN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_bus_operator)

        busOperators = mutableListOf()

        autoNhaXe = findViewById(R.id.autoNhaXe)
        autoNhaXe.text = null

        addBusOperatorBtn = findViewById(R.id.adminBusOperatorAddBtn)
        backBtn = findViewById(R.id.adminBusOperatorListBackBtn)
        val intent = Intent(this, AdminBusOperatorCreateActivity::class.java)
        addBusOperatorBtn.setOnClickListener {
                startActivityForResult(intent, REQUEST_CODE)
            }

        backBtn.setOnClickListener{
            finish()
        }
        }

    override fun onResume() {
        super.onResume()
        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }

        var page = 0
        var limit = 10
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

                    busOperatorAdapter = AdminBusOperatorAdapter(busOperators)
                    busOperatorRV = findViewById(R.id.adminBusOperatorRV)
                    busOperatorRV.adapter = busOperatorAdapter
                    busOperatorRV.layoutManager = LinearLayoutManager(this@AdminBusOperatorActivity,
                        LinearLayoutManager.VERTICAL,false)

                    val listName = busOperators.map { it.name }
                    val adapter = ArrayAdapter(
                        this@AdminBusOperatorActivity,
                        android.R.layout.simple_list_item_1,
                        listName
                    )
                    autoNhaXe.setAdapter(adapter)
                    autoNhaXe.addTextChangedListener(object: TextWatcher{
                        override fun beforeTextChanged(
                            p0: CharSequence?,
                            p1: Int,
                            p2: Int,
                            p3: Int
                        ) {}
                        override fun afterTextChanged(p0: Editable?) {}

                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            if (p0?.toString()!!.isEmpty()) {
                                busOperatorAdapter = AdminBusOperatorAdapter(busOperators)
                                busOperatorRV.adapter = busOperatorAdapter
                            }
                            else {
                                val newBusOperator: MutableList<BusOperator>
                                newBusOperator = busOperators.filter { it-> it.name.contains(p0.toString()) } as MutableList<BusOperator>
                                busOperatorAdapter = AdminBusOperatorAdapter(newBusOperator)
                                busOperatorRV.adapter = busOperatorAdapter
                            }
                        }



                    })
                }

            }

            // DELETE 1 BOOKING
            busOperatorAdapter?.onButtonClick = {busOperator ->
                GlobalScope.launch (Dispatchers.IO) {
                    Log.d("Button clicked" , busOperator.id)

                    val result = retrofit.getAllBusOperators().deleteBusOperator(token, busOperator.id).awaitResponse()

                    withContext(Dispatchers.Main){
                        val pos = busOperators.indexOf(busOperator)
                        busOperatorAdapter?.notifyItemRemoved(pos)
                    }
                }


            }

        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val id = data?.getStringExtra("id")

            // Do something with the user input
            val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
                throwable.printStackTrace()
            }


            GlobalScope.launch (Dispatchers.IO + coroutineExceptionHandler) {
                var response = retrofit.getAllBusOperators().getBusOperator(token!!,id!!).awaitResponse() // CHANGE
                Log.d("Response", "vui 1" + response.message())
                // debug response
                Log.d("Response", response.toString())
                if(response.isSuccessful){
                    Log.d("Response", "vui 2")
                    val data = response.body()!!
                    Log.d("Response", data.toString())
                    busOperators.add(0,data)

                    Log.d("busTickets vui 1: ", busOperators.size.toString())

                    withContext(Dispatchers.Main){
                        busOperatorAdapter?.notifyItemInserted(0)

                    }

                }

            }
        }
    }
    }
