package com.example.kotlin

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.kotlin.jsonConvert.HistoryList
import com.example.kotlin.jsonConvert.User
import com.example.kotlin.jsonConvert.UserLogInRespone
import com.example.kotlin.jsonConvert.UserLogin
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.awaitResponse
import java.lang.reflect.Type


data class ListItemFormat(
    val id: String,
    val name: String
)
class HomePage : AppCompatActivity() {
    val retrofit = APIServiceImpl()
    lateinit var listBusStations: ArrayList<BusStation>
    lateinit var listBusOperators: ArrayList<BusOperator>
    lateinit var startPointEdit: EditText
    lateinit var endPointEdit: EditText
    lateinit var departureDateEdit: EditText
    lateinit var bottomNavigationView: BottomNavigationView
    var fileUpload = UploadFile()
    var currentBusStartPoint = ""
    var currentBusEndPoint = ""
    private val UserAPI = APIServiceImpl().userService()
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
            val imageUri = data?.data as Uri
            // upload image to firebase
            fileUpload.uploadImageToFirebase(imageUri)

            Log.d("imageURL", fileUpload.getImageURL())
        }
    }

    private fun endPointEditHandle(){
        FragmentOperatorFilter().apply {
            // convert listBusOperators to listFilter
            listFilter = ArrayList()
            for (i in listBusStations.indices) {
                listFilter.add(ListItemFormat(listBusStations[i].id, listBusStations[i].name))
            }
            // pass listBusOperators to FragmentOperatorFilter
            arguments = Bundle().apply {
                putSerializable("listFilter", listFilter)
                putString("titleName", "Chọn nơi đến")
                if(currentBusEndPoint != ""){
                    putString("defaultId", currentBusEndPoint)
                }
            }
            show(supportFragmentManager, FragmentOperatorFilter.TAG)

            // onItemClick with data
            onItemClick = onItemClick@{item ->
                if(item.id == ""){
                    currentBusEndPoint = ""
                    endPointEdit.setText("")

                    return@onItemClick
                }
                currentBusEndPoint = item.id
                endPointEdit.setText(item.name)
            }
        }
    }

    private fun startPointEditHandle(){
        FragmentOperatorFilter().apply {
            // convert listBusOperators to listFilter
            listFilter = ArrayList()
            for (i in listBusStations.indices) {
                listFilter.add(ListItemFormat(listBusStations[i].id, listBusStations[i].name))
            }
            // pass listBusOperators to FragmentOperatorFilter
            arguments = Bundle().apply {
                putSerializable("listFilter", listFilter)
                putString("titleName", "Chọn nơi xuất phát")

                if(currentBusStartPoint != ""){
                    putString("defaultId", currentBusStartPoint)
                }
            }
            show(supportFragmentManager, FragmentOperatorFilter.TAG)

            // onItemClick with data
            onItemClick = onItemClick@{item ->
                if(item.id == ""){
                    currentBusStartPoint = ""
                    startPointEdit.setText("")

                    return@onItemClick
                }
                currentBusStartPoint = item.id
                startPointEdit.setText(item.name)
            }
        }
    }

    private fun handleSelectDate(){
        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            departureDateEdit.setText("$month/$dayOfMonth/$year")

        }, 2023, 3, 25).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        getLocalData()

        val loginBtn = findViewById<TextView>(R.id.loginBtn)
        loginBtn.setOnClickListener {
            intent = Intent(this, LogInUp::class.java)
            startActivity(intent)
        }

        val startPointSelect = findViewById<LinearLayout>(R.id.startPointSelect)
        startPointEdit = findViewById<EditText>(R.id.startPointEdit)
        // onClickListener for startPointSelect
        startPointSelect.setOnClickListener {
            startPointEditHandle()
        }

        // onClick startPointEdit
        startPointEdit.setOnClickListener {
            startPointEditHandle()
        }

        val endPointSelect = findViewById<LinearLayout>(R.id.endPointSelect)
        endPointEdit = findViewById<EditText>(R.id.endPointEdit)
        // onClickListener for startPointSelect
        endPointSelect.setOnClickListener {
            endPointEditHandle()
        }

        // onClick endPointEdit
        endPointEdit.setOnClickListener {
            endPointEditHandle()
        }

        GlobalScope.launch (Dispatchers.IO) {
            val response = retrofit.getAllBusStations().getBusStations().awaitResponse()
            if(response.isSuccessful){
                listBusStations = response.body()?.data as ArrayList<BusStation>
            }

            val response2 = retrofit.busOperatorService().getBusOperators().awaitResponse()
            if(response2.isSuccessful){
                listBusOperators = response2.body()?.data as ArrayList<BusOperator>
                // push all bus operator to list
                listBusOperators.add(0, BusOperator("all", "", "", "All"))

            }
        }

        val departureDate = findViewById<LinearLayout>(R.id.departureDate)
        departureDateEdit = findViewById<EditText>(R.id.departureDateEdit)
        // onClickListener for departureDate
        departureDate.setOnClickListener {
            handleSelectDate()
        }

        departureDateEdit.setOnClickListener {
            handleSelectDate()
        }

        val searchBus = findViewById<Button>(R.id.searchBus)

        searchBus.setOnClickListener {
            val intent = Intent(this, BusSearch::class.java)
            val departureId = listBusStations.find { it.name == startPointEdit.text.toString() }?.id
            val destinationId = listBusStations.find { it.name == endPointEdit.text.toString() }?.id
            val outputDateString = departureDateEdit.text.toString()
            val fromToString = startPointEdit.text.toString() + " -> " + endPointEdit.text.toString()
            intent.putExtra("departureId", departureId)
            intent.putExtra("destinationId", destinationId)
            intent.putExtra("outputDateString", outputDateString)
            intent.putExtra("fromToString", fromToString)
            startActivity(intent)
        }

        val viewAllNews = findViewById<AppCompatButton>(R.id.viewAllNews)
        viewAllNews.setOnClickListener {
            val intent = Intent(this, BlogSeeAllActivity::class.java)
            startActivity(intent)
        }

        var blogPage = 1
        var blogLimit = 20
        var blogList = findViewById<RecyclerView>(R.id.news)
        try {
            GlobalScope.launch(Dispatchers.IO) {
                val response =
                    retrofit.getBlog().getBlogs(blogPage, blogLimit).awaitResponse()
                // debug response
                Log.d("Response", response.toString())
                if (response.isSuccessful) {
                    val body = response.body()
                    launch(Dispatchers.Main) {
                        if (body != null) {
                            val blogAdapter = CustomBlogItem(body.data)

                            blogList!!.adapter = blogAdapter

                            blogList.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL)

                            blogAdapter.onItemClick = {
                                val intent = Intent(this@HomePage, BlogDetailActivity::class.java)
                                intent.putExtra("blogId", it.id)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }


        /*Bottom Navigation Tab*/
        // Initialize and assign variable
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        // Set Home selected
        bottomNavigationView.selectedItemId = R.id.search
        // Perform item selected listener
        var intent: Intent
        bottomNavigationView.setOnNavigationItemSelectedListener{ menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    finish()
                    intent = Intent(this, HomePage::class.java)
                    startActivity(intent)
                    true
                }

                R.id.blog -> {
                    finish()
                    intent = Intent(this, BlogSeeAllActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.ticket -> {
                    finish()
                    intent = Intent(this, PersonalInformation::class.java)
                    startActivity(intent)
                    true
                }

                R.id.user -> {
                    finish()
                    intent = Intent(this, CaNhanActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }


        }
    }

    private fun getLocalData(){
        var gson = Gson()
        val localStore = getSharedPreferences("vexere", Context.MODE_PRIVATE)
        var str_json_user = localStore.getString("user", null)
        var token = localStore.getString("token", null)

        token?.let {
            var callLogIn: Call<HistoryList> = UserAPI.ticketHistory("Bearer ${token!!}",0,1)
            var respone: HistoryList? = WaitingAsyncClass(callLogIn).execute().get()

            //token còn dùng được
            if(respone != null) {
                val userType: Type = object : TypeToken<User?>() {}.type
                UserInformation.USER = gson.fromJson(str_json_user, userType)
                UserInformation.TOKEN = token
                Log.i("!23", UserInformation.USER!!.display_name!!)
            }
        }

    }
}