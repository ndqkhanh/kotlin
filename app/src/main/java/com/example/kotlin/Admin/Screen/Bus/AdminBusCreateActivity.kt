package com.example.kotlin.Admin.Screen.Bus

import android.app.*
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin.AdminBusCreateBody
import com.example.kotlin.BusOperator
import com.example.kotlin.BusStation
import com.example.kotlin.R
import com.example.kotlin.jsonConvert.Buses
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.internal.wait
import retrofit2.awaitResponse
import java.util.*


class AdminBusCreateActivity: AppCompatActivity() {
    private var startDatePickerDialog: DatePickerDialog? = null
    private var startTimePickerDialog: TimePickerDialog? = null
    private var endDatePickerDialog: DatePickerDialog? = null
    private var endTimePickerDialog: TimePickerDialog?= null

    lateinit var startTimeTimeButton: Button
    lateinit var endTimeTimeButton: Button
    lateinit var startTimeDateButton: Button
    lateinit var endTimeDateButton: Button
    lateinit var addBtn: Button
    lateinit var busOperatorEt: EditText
    lateinit var startPointEt: EditText
    lateinit var endPointEt: EditText
    lateinit var imageET: EditText
    lateinit var numOfSeatET: EditText
    lateinit var priceET: EditText
    lateinit var busTypeSpinner: Spinner


    private  var busType : MutableList<String> = mutableListOf("Bus 1", "Bus 2", "Bus 3")

    // Sample data
    lateinit var data : AdminBusCreateBody
    lateinit var busOperators: MutableList<BusOperator>
    lateinit var busStations: MutableList<BusStation>

    // data field
    var bo_id : String = ""
    var start_point : String = ""
    var end_point : String = ""
    var start_time: String = ""
    var end_time: String = ""
    var image_url: String = ""
    var policy: String = "Hello"
    var num_of_seats : Int = 0
    var price: Int = 0
    var type: Int = 0

    // TODO Fragment not activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_bus_create)
//        val token = "BEARER " + this.getSharedPreferences("vexere", MODE_PRIVATE).getString("token", "")
//        val retrofit = APIServiceImpl()
//
//        addBtn = findViewById(R.id.addBtnAdminBusCreateBtn)
//        startPointEt = findViewById(R.id.adminBusStartPointCreateEt)
//        endPointEt = findViewById(R.id.adminBusEndPointCreateEt)
//        busOperatorEt = findViewById(R.id.adminBusBOCreateEt)
//        busTypeSpinner = findViewById(R.id.adminBusTypeCreateSpinner)
//        imageET = findViewById(R.id.adminBusImageCreateET)
//        numOfSeatET = findViewById(R.id.adminBusSeatCreateET)
//        priceET = findViewById(R.id.adminBusPriceCreateET)
//        startTimeDateButton = findViewById(R.id.adminCreateBusStartTimeDatePickerBtn)
//        endTimeDateButton = findViewById(R.id.adminCreateBusEndTimeDatePickerBtn)
//        startTimeTimeButton = findViewById(R.id.adminCreateBusStartTimeTimePickerBtn)
//        endTimeTimeButton = findViewById(R.id.adminCreateBusEndTimeTimePickerBtn)
//        val boCreateBtn = findViewById<Button>(R.id.adminBusBOCreateBtn)
//        val bsStartPointCreateBtn = findViewById<Button>(R.id.adminBusStartPointCreateBtn)
//        val bsEndPointCreateBtn = findViewById<Button>(R.id.adminBusEndPointCreateBtn)
//        initDatePicker()
//        startTimeDateButton.setText(getTodaysDate())
//        endTimeDateButton.setText(getTodaysDate())
//
//        initTimePicker()
//        startTimeTimeButton.setText(getTime())
//        endTimeTimeButton.setText(getTime())
//        busStations = mutableListOf()
//        busOperators = mutableListOf()


        // NOTE: Integrate into bus type spinner
//        val busTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, busType)
//        busTypeSpinner.adapter = busTypeAdapter
//
//
//
//        boCreateBtn.setOnClickListener {
//            showBODialog("bus operator")
//        }
//
//        bsStartPointCreateBtn.setOnClickListener {
//            showBSDialog("startPoint")
//        }
//
//        bsEndPointCreateBtn.setOnClickListener {
//            showBSDialog("endPoint")
//        }

        // Create sample data
//        sampleData = AdminBusCreateBody(
//            "fbb09364-3e67-46e2-bf0f-95670d827544",
//            "402bdf26-013e-4438-9813-b4d8c326e60d",
//            "402bdf26-013e-4438-9813-b4d8c326e60d",
//            0,
//            "01/03/2022",
//            "01/05/2022",
//            "https://loremflickr.com/300/200",
//            "Hello",
//            23,
//            53000
//        )


//        addBtn.setOnClickListener {
//            val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
//                throwable.printStackTrace()
//            }
            // THIS LOOK LIKE I CAN CALL API SUCCESSFULLY BUT REPONSE IS NOT HANDLE CORRECTLY
//            GlobalScope.launch (Dispatchers.IO + coroutineExceptionHandler) {
//                 start_time = convertTimeVal(startTimeDateButton.text.toString(), startTimeTimeButton.text.toString())
//                 end_time = convertTimeVal(endTimeDateButton.text.toString() ,endTimeTimeButton.text.toString())
//                 image_url = imageET.text.toString()
//                 policy = "Hello"
//                if (numOfSeatET.text.toString() != "")
//                 num_of_seats = Integer.parseInt(numOfSeatET.text.toString())
//                if (priceET.text.toString() != "")
//                    price = Integer.parseInt(priceET.text.toString())
//                when ( busTypeSpinner.selectedItem?.toString()){
//                    "Bus 1" -> type = 1
//                    "Bus 2" -> type = 2
//                    "Bus 3" -> type = 3
//                }
////                 type = busTypeSpinner.selectedItem?.toString()?.toInt()!!
//
//                data = AdminBusCreateBody(
//                bo_id,
//                    start_point,
//                    end_point,
//                    type,
//                    start_time,
//                    end_time,
//                    image_url,
//                    policy,
//                    num_of_seats,
//                    price
//        )
//                Log.d("data", data.toString())
//
//                val response = retrofit.adminCreateBus().adminCreateBus(token,data).awaitResponse()
//                Log.d("Response", "vui 1" + response.message())
//                // debug response
//                Log.d("Response", response.toString())
//                if(response.isSuccessful){
//                    Log.d("Response", "vui 2")
//                    val data = response.body()!!
//                    Log.d("Response", data.toString())
//
////                    val intent = Intent(this@AdminBusCreateActivity, AdminBusActivity::class.java )
////                    intent.putExtra("id", data.id)
////                    startActivity(intent)
//                    val intent = Intent()
//                    intent.putExtra("id", data.id)
//                    setResult(Activity.RESULT_OK, intent)
//                        finish()
//
//                }
//            }
        }
    }

//    private fun showBODialog(title: String) {
//        val dialog = Dialog(this)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setCancelable(false)
//        dialog.setContentView(R.layout.bus_operator_dialog)
//        val image = dialog.findViewById<ImageView>(R.id.boDiaglogImgV)
//        image.setImageResource(android.R.drawable.star_big_on)
//        val busOperatorRV = dialog.findViewById<RecyclerView>(R.id.boDiaglogRV)
//
//        val retrofit = APIServiceImpl()
//
//        GlobalScope.launch(Dispatchers.IO) {
//            val response = retrofit.getAllBusOperators().getBusOperators().awaitResponse()
//            // debug response
//            Log.d("Response", response.toString())
//            if (response.isSuccessful) {
//                val data = response.body()!!.data
//                Log.d("Response", data.toString())
//
//                for (bo in data) busOperators.add(bo)
//                Log.d("bo data ", busOperators.toString())
//
//                withContext(Dispatchers.Main){
//                    var busOperatorAdapter = BusOperatorAdapter( busOperators)
//                    busOperatorRV.adapter = busOperatorAdapter
//                    busOperatorRV.layoutManager = LinearLayoutManager (this@AdminBusCreateActivity, LinearLayoutManager.VERTICAL,false)
//
//
//                    busOperatorAdapter.onItemClick = { busOperator ->
//                        Log.d("busOperator", busOperator.toString())
//                        busOperatorEt.setText(busOperator.name)
//                        bo_id = busOperator.id
//                        dialog.dismiss()
//                    }
//                }
//
//
//            }
//        }
//
//
//
//        val cancelBtn = dialog.findViewById(R.id.boDiaglogCancelBtn) as Button
//        val okBtn = dialog.findViewById(R.id.boDiaglogOKBtn) as Button
//        cancelBtn.setOnClickListener {
//            dialog.dismiss()
//        }
//        okBtn.setOnClickListener {
//            dialog.dismiss()
//        }
//        dialog.show()
//
//    }
//
//    private fun showBSDialog(title: String) {
//        val dialog = Dialog(this)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setCancelable(false)
//        dialog.setContentView(R.layout.bus_station_dialog)
//        val image = dialog.findViewById<ImageView>(R.id.bsDiaglogImgV)
//        image.setImageResource(android.R.drawable.star_big_on)
//        val busStationRV = dialog.findViewById<RecyclerView>(R.id.bsDiaglogRV)
//
//        val retrofit = APIServiceImpl()
//
//        GlobalScope.launch(Dispatchers.IO) {
//            val response = retrofit.getAllBusStations().getBusStations().awaitResponse()
//            // debug response
//            Log.d("Response", response.toString())
//            if (response.isSuccessful) {
//                val data = response.body()!!.data
//                Log.d("Response", data.toString())
//
//                for (bo in data) busStations.add(bo)
//                Log.d("bo data ", busStations.toString())
//
//                withContext(Dispatchers.Main){
//                    var busStationAdapter = BusStationAdapter( busStations)
//                    busStationRV.adapter = busStationAdapter
//                    busStationRV.layoutManager = LinearLayoutManager (this@AdminBusCreateActivity, LinearLayoutManager.VERTICAL,false)
//
//
//                    busStationAdapter.onItemClick = { busStation ->
//                        Log.d("busOperator", busStation.toString())
//                        if (title == "startPoint")
//                        {
//                            startPointEt.setText(busStation.name + ", " + busStation.location)
//                            start_point = busStation.id
//                        }
//                        else {
//                            endPointEt.setText(busStation.name + ", " + busStation.location)
//                            end_point = busStation.id
//                        }
//                        dialog.dismiss()
//                    }
//                }
//
//
//            }
//        }
//
//
//
//        val cancelBtn = dialog.findViewById(R.id.bsDiaglogCancelBtn) as Button
//        val okBtn = dialog.findViewById(R.id.bsDiaglogOKBtn) as Button
//        cancelBtn.setOnClickListener {
//            dialog.dismiss()
//        }
//        okBtn.setOnClickListener {
//            dialog.dismiss()
//        }
//        dialog.show()
//
//    }
//    private fun getTime(): String {
//        val c = Calendar.getInstance()
//        val hour = c.get(Calendar.HOUR_OF_DAY)
//        val minute = c.get(Calendar.MINUTE)
//        return hour.toString() + ":" + minute
//    }

//    private fun initTimePicker() {
//
//        val c = Calendar.getInstance()
//        val hour = c.get(Calendar.HOUR_OF_DAY)
//        val minute = c.get(Calendar.MINUTE)
//        val startTimeTimepicker = TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
//            startTimeTimeButton.setText(i.toString() + ":" + i2.toString())
//
//        }
//        val endTimeTimepicker = TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
//            endTimeTimeButton.setText(i.toString() + ":" + i2.toString())
//
//        }
//
//        startTimePickerDialog = TimePickerDialog(this,startTimeTimepicker,hour,minute,true)
//        endTimePickerDialog = TimePickerDialog(this,endTimeTimepicker,hour,minute,true)
//    }


    private fun getTodaysDate(): String? {
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        var month: Int = cal.get(Calendar.MONTH)
        month = month + 1
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        return makeDateString(day, month, year)
    }

//    private fun initDatePicker() {
//        val startDateSetListener =
//            OnDateSetListener { datePicker, year, month, day ->
//                var month = month
//                month = month + 1
//                val date = makeDateString(day, month, year)
//                startTimeDateButton.setText(date)
//
//            }
//        val endDateSetListener =
//            OnDateSetListener { datePicker, year, month, day ->
//                var month = month
//                month = month + 1
//                val date = makeDateString(day, month, year)
//                endTimeDateButton.setText(date)
//
//            }
//        val cal: Calendar = Calendar.getInstance()
//        val year: Int = cal.get(Calendar.YEAR)
//        val month: Int = cal.get(Calendar.MONTH)
//        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
//        val style: Int = AlertDialog.THEME_HOLO_LIGHT
//        startDatePickerDialog = DatePickerDialog(this, style, startDateSetListener, year, month, day)
//        endDatePickerDialog = DatePickerDialog(this, style, endDateSetListener, year, month, day)
//    //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
//    }

    private fun makeDateString(day: Int, month: Int, year: Int): String? {
        return getMonthFormat(month) + " " + day + " " + year
    }

    private fun getMonthFormat(month: Int): String {
        if (month == 1) return "JAN"
        if (month == 2) return "FEB"
        if (month == 3) return "MAR"
        if (month == 4) return "APR"
        if (month == 5) return "MAY"
        if (month == 6) return "JUN"
        if (month == 7) return "JUL"
        if (month == 8) return "AUG"
        if (month == 9) return "SEP"
        if (month == 10) return "OCT"
        if (month == 11) return "NOV"
        return if (month == 12) "DEC" else "JAN"

        //default should never happen
    }

//    fun openStartDatePicker(view: View?) {
//        startDatePickerDialog?.show()
//    }
//
//    fun openStartTimePicker(view: View?){
//        startTimePickerDialog?.show()
//    }
//
//    fun openEndDatePicker(view: View?) {
//        endDatePickerDialog?.show()
//    }
//
//    fun openEndTimePicker(view: View?){
//        endTimePickerDialog?.show()
//
//    }
    fun convertTimeVal(date: String, time: String): String{
        var value: String = ""
        var s = date.split(" ")
        value += s[2]

        when (s[0]){
            "JAN" -> value += "-01"
            "FEB" -> value += "-02"
            "MAR" -> value += "-03"
            "APR" -> value += "-04"
            "MAY" -> value += "-05"
            "JUN" -> value += "-06"
            "JUL" -> value += "-07"
            "AUG" -> value += "-08"
            "SEP" -> value += "-09"
            "OCT" -> value += "-10"
            "NOV" -> value += "-11"
            "DEC" -> value += "-12"
        }

        value += "-" + s[1] + " "

        // FORMAT IS HH:MM so add 0 before h:mm
        s = time.split(":")
        var hh = s[0]
        var mm = s[1]
        if (hh.length != 2 ) hh = "0" + hh
        if (mm.length != 2 ) mm = "0" + mm

        value += hh + ":" + mm
        return value

    }


