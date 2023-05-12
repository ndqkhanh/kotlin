package com.example.kotlin.Admin.Screen.Bus

import android.app.*
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import com.example.kotlin.*
import com.example.kotlin.utils.UploadFile
import com.example.kotlin.utils.UserInformation
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import retrofit2.awaitResponse
import java.util.*


class AdminBusCreateActivity: AppCompatActivity() {
    private var startDatePickerDialog: DatePickerDialog? = null
    private var startTimePickerDialog: TimePickerDialog? = null
    private var endDatePickerDialog: DatePickerDialog? = null
    private var endTimePickerDialog: TimePickerDialog? = null

    lateinit var startTimeTimeButton: Button
    lateinit var endTimeTimeButton: Button
    lateinit var startTimeDateButton: Button
    lateinit var endTimeDateButton: Button
    lateinit var backBtn: ImageButton
    lateinit var addBtn: Button
    lateinit var busOperatorEt: AutoCompleteTextView
    lateinit var startPointEt: AutoCompleteTextView
    lateinit var endPointEt: AutoCompleteTextView
    lateinit var image: ImageView
    lateinit var numOfSeatET: EditText
    lateinit var priceET: EditText
    lateinit var busTypeAutoCompleteTV: AutoCompleteTextView
    private var photoChosen = false
    private var photoUri: Uri? = null
    private var fileUpload = UploadFile()
    private lateinit var edtContent: com.google.android.material.textview.MaterialTextView

    private var busType: MutableList<String> = mutableListOf("Ghế ngồi", "Giường nằm", "Giường nằm đôi")

    // Sample data
    lateinit var data: AdminBusCreateBody
    lateinit var busOperators: MutableList<BusOperator>
    lateinit var busStations: MutableList<BusStation>
    lateinit var points: MutableList<PointStation>

    // data field
    var bo_id: String = ""
    var start_point: String = ""
    var end_point: String = ""
    var start_time: String = ""
    var end_time: String = ""
    var image_url: String = ""
    var policy: String = "Hello"
    var num_of_seats: Int = 0
    var price: Int = 0
    var type: Int = 0
    val token = "BEARER " + UserInformation.TOKEN
    val retrofit = APIServiceImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_bus_create)


        addBtn = findViewById(R.id.addBtnAdminBusCreateBtn)
        startPointEt = findViewById(R.id.startPointAutoCompleteTextView)
        endPointEt = findViewById(R.id.endPointAutoCompleteTextView)
        busOperatorEt = findViewById(R.id.busOperatorAutoCompleteTextView)
        busTypeAutoCompleteTV = findViewById(R.id.busTypeAutoCompleteTextView)
        image = findViewById(R.id.imageAdminBusThumbnail)
        numOfSeatET = findViewById(R.id.adminBusSeatCreateET)
        priceET = findViewById(R.id.adminBusPriceCreateET)
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
        busStations = mutableListOf()
        busOperators = mutableListOf()
        points = mutableListOf()

        backBtn = findViewById(R.id.adminBusListBackBtn)
        backBtn.setOnClickListener { finish() }




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

        var page = 0
        var limit = 50
        // get type of bus
        val busTypeAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, busType)
        busTypeAutoCompleteTV.setAdapter(busTypeAdapter)
        busTypeAutoCompleteTV.setOnItemClickListener { adapterView, view, i, l ->
            Toast.makeText(
                this@AdminBusCreateActivity,
                adapterView.getItemAtPosition(i).toString(),
                Toast.LENGTH_SHORT
            ).show()
            when (adapterView.getItemAtPosition(i)){
                "Ghế ngồi" -> type = 0
                "Giường nằm" -> type = 1
                "Giường nằm đôi" -> type = 2
            }
        }

        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }

        // get bus operator from autocompletetextview
        GlobalScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            var responseBO = APIServiceImpl().getAllBusOperators().getBusOperators(token).awaitResponse()
            val listBO = mutableListOf<String>()
            if (responseBO.isSuccessful) {
                var data = responseBO.body()!!
                data.data.forEach { item ->
                    busOperators.add(item)
                    listBO.add(item.name)
                }

            }


            withContext(Dispatchers.Main) {
                val boAdapter = ArrayAdapter(
                    this@AdminBusCreateActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    listBO
                )
                busOperatorEt.setAdapter(boAdapter)
                busOperatorEt.setOnItemClickListener { adapterView, view, i, l ->
                    Toast.makeText(
                        this@AdminBusCreateActivity,
                        adapterView.getItemAtPosition(i).toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    bo_id = busOperators.get(i).id
                }


            }


        }

        // get start point, end point from autocompletetextview
        GlobalScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            var responseBS = APIServiceImpl().getAllBusStations().getBusStations().awaitResponse()
            val list = mutableListOf<String>()
            if (responseBS.isSuccessful) {
                var data = responseBS.body()!!
                data.data.forEach { item ->
                    busStations.add(item)
                    list.add(item.name)
                }

            }
            withContext(Dispatchers.Main) {
                val pointAdapter = ArrayAdapter(
                    this@AdminBusCreateActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    list
                )
                startPointEt.setAdapter(pointAdapter)
                startPointEt.setOnItemClickListener { adapterView, view, i, l ->
                    Toast.makeText(
                        this@AdminBusCreateActivity,
                        adapterView.getItemAtPosition(i).toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    start_point = busStations.get(i).id
                }

                endPointEt.setAdapter(pointAdapter)
                endPointEt.setOnItemClickListener { adapterView, view, i, l ->
                    Toast.makeText(
                        this@AdminBusCreateActivity,
                        adapterView.getItemAtPosition(i).toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    end_point = busStations.get(i).id
                }
            }


        }

        // Upload image
        val btnUpload = findViewById<Button>(R.id.adminBusBtnUpload)
        btnUpload.setOnClickListener {
            selectImage()
        }


        edtContent = findViewById(R.id.edtPolicy)
        // intent to EditText Detail Activity when click on edtContent
        edtContent.setOnClickListener {
            val intent = Intent(this, EditTextDetailActivity::class.java)
            intent.putExtra("content", HtmlCompat.toHtml(edtContent.text as Spanned, HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE))
            startActivityForResult(intent, 567)
        }

        // add new bus
        addBtn.setOnClickListener {
            // get start time, end time
            start_time = convertTimeVal(
                startTimeDateButton.text.toString(),
                startTimeTimeButton.text.toString()
            )
            end_time = convertTimeVal(
                endTimeDateButton.text.toString(),
                endTimeTimeButton.text.toString()
            )

            Log.d("start_time ", start_time)


            policy = edtContent.text.toString()

            // get number of seats
            if (numOfSeatET.text.toString() != "")
                num_of_seats = Integer.parseInt(numOfSeatET.text.toString())

            // get price
            if (priceET.text.toString() != "")
                price = Integer.parseInt(priceET.text.toString())

            if (!photoChosen || photoUri == null) {
                Toast.makeText(this, "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if(bo_id.isEmpty() || start_point.isEmpty() || end_point.isEmpty() || start_time.isEmpty() || end_time.isEmpty() || num_of_seats == 0 || price == 0 || policy.isEmpty()){
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val bottomSheetDialog = BottomSheetDialog(
                this, com.google.android.material.R.style.Theme_Design_BottomSheetDialog
            )
            val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
                R.layout.layout_payment_bottom_sheet,
                findViewById<ConstraintLayout>(R.id.bottomSheet)
            )

            bottomSheetView.findViewById<TextView>(R.id.txtTitle).text = "Bạn có chắc thêm chuyến xe này không?"

            bottomSheetView.findViewById<TextView>(R.id.txtMessage).text = "Hành động này không thể hoàn tác. Hãy chắc chắn rằng bạn đã kiểm tra kỹ thông tin trước khi thêm chuyến xe này."

            bottomSheetView.findViewById<Button>(R.id.btnBack).setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            bottomSheetView.findViewById<Button>(R.id.btnPay).text = "Tiếp tục thêm chuyến xe"


            bottomSheetView.findViewById<Button>(R.id.btnPay).setOnClickListener {
                val dialog = ProgressDialog(this)
                dialog.setMessage("Đang thêm chuyến xe...")
                dialog.setCancelable(false)
                dialog.show()


                try {
                    // wait for the upload task to complete
                    // turn on progress bar



                    val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
                        throwable.printStackTrace()

                    }


                    GlobalScope.launch (Dispatchers.IO + coroutineExceptionHandler) {

                        val downloadUri = fileUpload.uploadImageToFirebase(photoUri!!).await()
                        image_url = downloadUri.toString()
                        // form data to create new bus
                        data = AdminBusCreateBody(
                            bo_id,
                            start_point,
                            end_point,
                            type,
                            start_time,
                            end_time,
                            image_url,
                            policy,
                            num_of_seats,
                            price
                        )

                        val response = retrofit.adminService().createBus(token, data).awaitResponse()
                        Log.d("Response", "vui 1" + response.message())
                        // debug response
                        Log.d("Response", response.toString())
                        if(response.isSuccessful){
                            Log.d("Response", "vui 2")
                            val data = response.body()!!
                            Log.d("Response", data.toString())
                            launch(Dispatchers.Main) {
                                bottomSheetDialog.dismiss()
                                dialog.dismiss()
                                Toast.makeText(this@AdminBusCreateActivity, "Thêm chuyến xe thành công", Toast.LENGTH_SHORT).show()
                                val intent = Intent()
                                intent.putExtra("id", data.id)
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }
                        }
                        else {
                            launch(Dispatchers.Main) {
                                dialog.dismiss()
                                Toast.makeText(this@AdminBusCreateActivity, "Đã xảy ra lỗi, xin hãy kiểm tra lại kết nối", Toast.LENGTH_SHORT).show()
                            }
                        }


                    }


                    // Send information to list

                }catch (e: Exception){
                    dialog.dismiss()
                    Toast.makeText(this@AdminBusCreateActivity, "Đã xảy ra lỗi, xin hãy kiểm tra lại kết nối", Toast.LENGTH_SHORT).show()
                }
            }


            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()

        }



    }

    private fun selectImage(){
        // select image from local storage
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    // onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100){
            // get image from local storage
            val imageUri = data?.data
            if(imageUri != null) {

                image.setImageURI(imageUri)
                photoUri = imageUri
                photoChosen = true
            }
        }else if(requestCode == 567) {
            if (resultCode == Activity.RESULT_OK) {
                // get content from EditTextDetailActivity
                val content = data?.getStringExtra("content")
                edtContent.setText(content?.let {
                    HtmlCompat.fromHtml(
                        it,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                })
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

        startTimePickerDialog = TimePickerDialog(this, startTimeTimepicker, hour, minute, true)
        endTimePickerDialog = TimePickerDialog(this, endTimeTimepicker, hour, minute, true)
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
        startDatePickerDialog =
            DatePickerDialog(this, style, startDateSetListener, year, month, day)
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

    fun openStartTimePicker(view: View?) {
        startTimePickerDialog?.show()
    }

    fun openEndDatePicker(view: View?) {
        endDatePickerDialog?.show()
    }

    fun openEndTimePicker(view: View?) {
        endTimePickerDialog?.show()

    }

    fun convertTimeVal(date: String, time: String): String {
        var value: String = ""
        var s = date.split(" ")
        value += s[2]

        when (s[0]) {
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
        when (s[1]) {
            "1" -> value += "-01" + " "
            "2" -> value += "-02" + " "
            "3" -> value += "-03" + " "
            "4" -> value += "-04" + " "
            "5" -> value += "-05" + " "
            "6" -> value += "-06" + " "
            "7" -> value += "-07" + " "
            "8" -> value += "-08" + " "
            "9" -> value += "-09" + " "
            else -> value += "-" + s[1] + " "
        }


        // FORMAT IS HH:MM so add 0 before h:mm
        s = time.split(":")
        var hh = s[0]
        var mm = s[1]
        if (hh.length != 2) hh = "0" + hh
        if (mm.length != 2) mm = "0" + mm

        value += hh + ":" + mm
        return value

    }
}


