package com.example.kotlin

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
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
    lateinit var busOperatorSpinner: Spinner
    lateinit var startPointSpinner: Spinner
    lateinit var endPointSpinner: Spinner
    lateinit var busTypeSpinner: Spinner

    lateinit  var busStationData : MutableList<BusStation>
    lateinit var locationData: MutableList<String> // This just include location not id
    lateinit var busOperatorData: MutableList<BusOperator>
    lateinit var busOperatorNameData: MutableList<String> // This just includes name of bus op not id
    private  var busType : MutableList<String> = mutableListOf("Bus 1", "Bus 2", "Bus 3")
    // THIS WILL BE REPLACED WHENEVER HOANG LOGIN BY FACEBOOK
    // TOKEN WILL GET FROM THERE
    val token = "Bearer  eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI3ZmU0YTNlZS0zMjRiLTQ0NWQtODYzYy0wN2ZjNzAyYmQ4NDQiLCJpYXQiOjE2Nzk3MzM5MTQsImV4cCI6MTY3OTczNTcxNCwidHlwZSI6ImFjY2VzcyJ9.uQsh3SoCWeCoYe3l5yMUfMhiSbQDGtEjsSZrM2RPTuA"

    // Sample data
    lateinit var sampleData : AdminBusCreateBody


    // TODO Fragment not activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_bus_create)

        val retrofit = APIServiceImpl()

        addBtn = findViewById(R.id.addBtnAdminBusCreateBtn)
        startPointSpinner = findViewById(R.id.adminBusStartPointCreateSpinner)
        endPointSpinner = findViewById(R.id.adminBusEndPointCreateSpinner)
        busOperatorSpinner = findViewById(R.id.adminBusOpCreateSpinner)
        busTypeSpinner = findViewById(R.id.adminBusTypeCreateSpinner)
        startTimeDateButton = findViewById(R.id.adminCreateBusStartTimeDatePickerBtn)
        endTimeDateButton = findViewById(R.id.adminCreateBusEndTimeDatePickerBtn)
        startTimeTimeButton = findViewById(R.id.adminCreateBusStartTimeTimePickerBtn)
        endTimeTimeButton = findViewById(R.id.adminCreateBusEndTimeTimePickerBtn)
        initDatePicker()
        startTimeDateButton.setText(getTodaysDate())
        endTimeDateButton.setText(getTodaysDate())

        initTimePicker()
        startTimeTimeButton.setText(getTime())
        endTimeTimeButton.setText(getTime())

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


        // NOTE: Integrate into bus type spinner
        val busTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, busType)
        busTypeSpinner.adapter = busTypeAdapter
        // NOTE: Integrate spinner bus op
        busOperatorData = mutableListOf()
        busOperatorNameData = mutableListOf()
        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }


        GlobalScope.launch (Dispatchers.IO + coroutineExceptionHandler) {
            val response = retrofit.getAllBusOperators().getBusOperators().awaitResponse()
            Log.d("Response", "vui 1" + response.message())
            // debug response
            Log.d("Response", response.toString())
            if(response.isSuccessful){
                Log.d("Response", "vui 2")
                val data = response.body()!!
                Log.d("Response", data.toString())

                data.data.forEach{it ->
                    busOperatorData.add(it)
                    busOperatorNameData.add(it.name)
                }

            }
        }

        Log.d("bus op data",busOperatorData.size.toString())
        val busOperatorAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, busOperatorNameData)
        busOperatorSpinner.adapter = busOperatorAdapter

        // TODO Handle event
        busOperatorSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val boSelected =p0?.getItemAtPosition(p2) as String
                Log.i("Item", boSelected)
                Log.i("BO Data: ", busOperatorData[p2].toString())
                Toast.makeText(applicationContext,busOperatorData[p2].toString(),Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                print("Hello")
            }
        }



        // NOTE: Integrate start point
        // NOTE: Integrate end point spinner
        busStationData = mutableListOf()
        locationData = mutableListOf()


        GlobalScope.launch (Dispatchers.IO + coroutineExceptionHandler) {
            val response = retrofit.getAllBusStations().getBusStations().awaitResponse()
            Log.d("Response", "vui 1" + response.message())
            // debug response
            Log.d("Response", response.toString())
            if(response.isSuccessful){
                Log.d("Response", "vui 2")
                val data = response.body()!!
                Log.d("Response", data.toString())

                data.data.forEach{
                    busStationData.add(it)
                    locationData.add(it.name + ", " + it.location)
                }

            }
        }
        val busStationAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, locationData)
        startPointSpinner.adapter = busStationAdapter
        endPointSpinner.adapter = busStationAdapter

        // TODO Click event





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

    private fun getTime(): String {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        return hour.toString() + ":" + minute
    }

    private fun initTimePicker() {

        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val startTimeTimepicker = TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
            startTimeTimeButton.setText(i.toString() + ":" + i2.toString())

        }
        val endTimeTimepicker = TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
            endTimeTimeButton.setText(i.toString() + ":" + i2.toString())

        }

        startTimePickerDialog = TimePickerDialog(this,startTimeTimepicker,hour,minute,true)
        endTimePickerDialog = TimePickerDialog(this,endTimeTimepicker,hour,minute,true)
    }


    private fun getTodaysDate(): String? {
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        var month: Int = cal.get(Calendar.MONTH)
        month = month + 1
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        return makeDateString(day, month, year)
    }

    private fun initDatePicker() {
        val startDateSetListener =
            OnDateSetListener { datePicker, year, month, day ->
                var month = month
                month = month + 1
                val date = makeDateString(day, month, year)
                startTimeDateButton.setText(date)

            }
        val endDateSetListener =
            OnDateSetListener { datePicker, year, month, day ->
                var month = month
                month = month + 1
                val date = makeDateString(day, month, year)
                endTimeDateButton.setText(date)

            }
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        val style: Int = AlertDialog.THEME_HOLO_LIGHT
        startDatePickerDialog = DatePickerDialog(this, style, startDateSetListener, year, month, day)
        endDatePickerDialog = DatePickerDialog(this, style, endDateSetListener, year, month, day)
    //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

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

    fun openStartDatePicker(view: View?) {
        startDatePickerDialog?.show()
    }

    fun openStartTimePicker(view: View?){
        startTimePickerDialog?.show()
    }

    fun openEndDatePicker(view: View?) {
        endDatePickerDialog?.show()
    }

    fun openEndTimePicker(view: View?){
        endTimePickerDialog?.show()

    }

    override fun onResume() {
        super.onResume()

    // TODO INTEGRATE CREATE BUS


    }
}