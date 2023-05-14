package com.example.kotlin.Admin.Screen.BusOperator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin.*
import com.example.kotlin.utils.UserInformation
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.*
import retrofit2.awaitResponse

class AdminBusOperatorActivity:AppCompatActivity() {
    lateinit var busOperatorRV: RecyclerView
    lateinit var busOperators: MutableList<BusOperator>
    lateinit var backBtn: ImageButton
    lateinit var addBusOperatorBtn: Button
    private lateinit var autoNhaXe: AutoCompleteTextView
    var busOperatorAdapter: AdminBusOperatorAdapter? = null
    val REQUEST_CODE = 1111
    val retrofit = APIServiceImpl
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

        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }

        var page = 0
        var limit = 10
        GlobalScope.launch (Dispatchers.IO + coroutineExceptionHandler) {
            if (!busOperators.isEmpty()) busOperators.clear()
            var response = retrofit.adminService().getBusOperators(token).awaitResponse()
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
                            var newBusOperator: MutableList<BusOperator> = mutableListOf()
                            if (p0?.toString()!!.isEmpty()) {
                                newBusOperator.addAll(busOperators)
                            }
                            else {
                                newBusOperator = busOperators.filter { it-> it.name.lowercase().contains(p0.toString().lowercase().trim()) } as MutableList<BusOperator>
                            }

                            busOperatorAdapter = AdminBusOperatorAdapter(newBusOperator)
                            busOperatorRV.adapter = busOperatorAdapter

                            // DELETE 1 BOOKING
                            busOperatorAdapter?.onButtonClick = {busOperator ->
                                val bottomSheetDialog = BottomSheetDialog(
                                    this@AdminBusOperatorActivity, com.google.android.material.R.style.Theme_Design_BottomSheetDialog
                                )
                                val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
                                    R.layout.layout_payment_bottom_sheet,
                                    findViewById<ConstraintLayout>(R.id.bottomSheet)
                                )

                                bottomSheetView.findViewById<TextView>(R.id.txtTitle).text = "Bạn có chắc xóa nhà xe này không?"

                                bottomSheetView.findViewById<TextView>(R.id.txtMessage).text = "Hành động này không thể hoàn tác. Hãy chắc chắn rằng bạn đã kiểm tra kỹ thông tin trước khi xóa nhà xe này."

                                bottomSheetView.findViewById<Button>(R.id.btnBack).setOnClickListener {
                                    bottomSheetDialog.dismiss()
                                }

                                bottomSheetView.findViewById<Button>(R.id.btnPay).text = "Tiếp tục xóa nhà xe"

                                bottomSheetView.findViewById<Button>(R.id.btnPay).setOnClickListener{
                                    try {
                                        GlobalScope.launch (Dispatchers.IO) {
                                            Log.d("Button clicked" , busOperator.id)

                                            val result = APIServiceImpl().getAllBusOperators().deleteBusOperator(token, busOperator.id).awaitResponse()

                                            if (result.isSuccessful){
                                                withContext(Dispatchers.Main){
                                                    var pos = newBusOperator.indexOf(busOperator)
                                                    newBusOperator.removeAt(pos)
                                                    busOperatorAdapter?.notifyItemRemoved(pos)

                                                    pos = busOperators.indexOf(busOperator)
                                                    busOperators.removeAt(pos)
                                                }
                                                launch(Dispatchers.Main) {
                                                    Toast.makeText(this@AdminBusOperatorActivity, "Xóa nhà xe thành công", Toast.LENGTH_SHORT).show()
                                                    bottomSheetDialog.dismiss()
                                                }
                                            }
                                            else {
                                                launch(Dispatchers.Main) {
                                                    if(response.code() == 401){
                                                        Toast.makeText(
                                                            this@AdminBusOperatorActivity,
                                                            "Phiên đăng nhập đã hết hạn.\nVui lòng đăng nhập lại.",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            }

                                        }

                                    }
                                    catch(e: Exception){
                                        Toast.makeText(this@AdminBusOperatorActivity, "Đã xảy ra lỗi, xin hãy kiểm tra lại kết nối", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                bottomSheetDialog.setContentView(bottomSheetView)
                                bottomSheetDialog.show()
                            }
                        }



                    })
                }

            }

            // DELETE 1 BOOKING
            busOperatorAdapter?.onButtonClick = {busOperator ->
                val bottomSheetDialog = BottomSheetDialog(
                    this@AdminBusOperatorActivity, com.google.android.material.R.style.Theme_Design_BottomSheetDialog
                )
                val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
                    R.layout.layout_payment_bottom_sheet,
                    findViewById<ConstraintLayout>(R.id.bottomSheet)
                )

                bottomSheetView.findViewById<TextView>(R.id.txtTitle).text = "Bạn có chắc xóa nhà xe này không?"

                bottomSheetView.findViewById<TextView>(R.id.txtMessage).text = "Hành động này không thể hoàn tác. Hãy chắc chắn rằng bạn đã kiểm tra kỹ thông tin trước khi xóa nhà xe này."

                bottomSheetView.findViewById<Button>(R.id.btnBack).setOnClickListener {
                    bottomSheetDialog.dismiss()
                }

                bottomSheetView.findViewById<Button>(R.id.btnPay).text = "Tiếp tục xóa nhà xe"

                bottomSheetView.findViewById<Button>(R.id.btnPay).setOnClickListener{
                    try {
                        GlobalScope.launch (Dispatchers.IO) {
                            Log.d("Button clicked" , busOperator.id)

                            val result = APIServiceImpl().getAllBusOperators().deleteBusOperator(token, busOperator.id).awaitResponse()

                            if (result.isSuccessful){
                                withContext(Dispatchers.Main){
                                    val pos = busOperators.indexOf(busOperator)
                                    busOperators.removeAt(pos)
                                    busOperatorAdapter?.notifyItemRemoved(pos)
                                }
                                launch(Dispatchers.Main) {
                                    Toast.makeText(this@AdminBusOperatorActivity, "Xóa nhà xe thành công", Toast.LENGTH_SHORT).show()
                                    bottomSheetDialog.dismiss()
                                }
                            }
                            else {
                                launch(Dispatchers.Main) {
                                    if(response.code() == 401){
                                        Toast.makeText(
                                            this@AdminBusOperatorActivity,
                                            "Phiên đăng nhập đã hết hạn.\nVui lòng đăng nhập lại.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }

                        }

                    }
                    catch(e: Exception){
                        Toast.makeText(this@AdminBusOperatorActivity, "Đã xảy ra lỗi, xin hãy kiểm tra lại kết nối", Toast.LENGTH_SHORT).show()
                    }
                }

                bottomSheetDialog.setContentView(bottomSheetView)
                bottomSheetDialog.show()
            }
            // DELETE 1 BOOKING

        }
        }

    override fun onResume() {
        super.onResume()

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
                var response = APIServiceImpl().getAllBusOperators().getBusOperator(token!!,id!!).awaitResponse() // CHANGE

                if(response.isSuccessful){
                    val data = response.body()!!
                    Log.d("nhà xe mới thêm", data.toString())
                    busOperators.add(0,data)


//                    withContext(Dispatchers.Main){
//                        busOperatorAdapter?.notifyItemInserted(0)
//
//                    }

                }

            }
        }
    }
    }
