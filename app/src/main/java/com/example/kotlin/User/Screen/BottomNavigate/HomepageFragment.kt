package com.example.kotlin

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.kotlin.User.Screen.BottomNavigate.BlogFragment
import com.example.kotlin.User.Screen.BottomNavigate.BottomNavigation.Companion.replaceWithBlogFragment
import com.example.kotlin.Adapter.CustomBlogItem
import com.example.kotlin.User.Screen.BusSearch
import com.example.kotlin.utils.UploadFile
import com.example.kotlin.UserAdmin.Screen.LogInUp
import com.example.kotlin.utils.UserInformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class ListItemFormat(
    val id: String,
    val name: String
)
class HomepageFragment : Fragment() {
    val retrofit = APIServiceImpl()
    lateinit var listBusStations: ArrayList<BusStation>
    lateinit var listBusOperators: ArrayList<BusOperator>
    lateinit var startPointEdit: EditText
    lateinit var endPointEdit: EditText
    lateinit var departureDateEdit: EditText
    var fileUpload = UploadFile()
    var currentBusStartPoint = ""
    var currentBusEndPoint = ""
    lateinit var loginBtn: TextView
    lateinit var startPointSelect: LinearLayout
    lateinit var endPointSelect: LinearLayout
    lateinit var departureDate: LinearLayout
    lateinit var searchBus: Button
    lateinit var viewAllNews: AppCompatButton
    lateinit var blogList: RecyclerView
    val token = "BEARER " + UserInformation.TOKEN
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
        val endPointFragment = FragmentOperatorFilter().apply {
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

        endPointFragment.show(this.parentFragmentManager, FragmentOperatorFilter.TAG)
    }

    private fun startPointEditHandle(){
        val startPointFragment = FragmentOperatorFilter().apply {
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

        startPointFragment.show(this.parentFragmentManager, FragmentOperatorFilter.TAG)
    }

    private fun handleSelectDate(){
        val current = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"))
        val currentYear = current.year
        val currentMonth = current.monthValue
        val currentDay = current.dayOfMonth
        DatePickerDialog(this.requireContext(), DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            // check if choose a date before today
            val date = LocalDate.of(year, month + 1, dayOfMonth)
            val today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"))
            if(date.isBefore(today)){
                Toast.makeText(this.requireContext(), "Ngày không được trong quá khứ", Toast.LENGTH_SHORT).show()
                departureDateEdit.setText("")
                return@OnDateSetListener
            }

            departureDateEdit.setText("${month + 1}/$dayOfMonth/$year")

        }, currentYear, currentMonth - 1, currentDay).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_homepage, container, false)
        loginBtn = rootView.findViewById(R.id.loginBtn)
        startPointSelect = rootView.findViewById(R.id.startPointSelect)
        startPointEdit = rootView.findViewById(R.id.startPointEdit)
        endPointSelect = rootView.findViewById(R.id.endPointSelect)
        endPointEdit = rootView.findViewById(R.id.endPointEdit)
        departureDate = rootView.findViewById(R.id.departureDate)
        departureDateEdit = rootView.findViewById(R.id.departureDateEdit)
        searchBus = rootView.findViewById(R.id.searchBus)
        viewAllNews = rootView.findViewById(R.id.viewAllNews)
        blogList = rootView.findViewById(R.id.news)


        return rootView
    }

    override fun onResume() {
        super.onResume()
        if(UserInformation.TOKEN != null)
            loginBtn.visibility = GONE
        else
            loginBtn.visibility = VISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginBtn.setOnClickListener {
            var intentLogIn = Intent(requireContext(), LogInUp::class.java)
            startActivity(intentLogIn)
        }
        if(UserInformation.TOKEN != null)
            loginBtn.visibility = GONE
        else
            loginBtn.visibility = VISIBLE

        // onClickListener for startPointSelect
        startPointSelect.setOnClickListener {
            startPointEditHandle()
        }

        // onClick startPointEdit
        startPointEdit.setOnClickListener {
            startPointEditHandle()
        }

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

                withContext(Dispatchers.Main){
                    // push all bus station to list
                    if(listBusStations.size > 1){
                        currentBusStartPoint = listBusStations[0].id
                        startPointEdit.setText(listBusStations[0].name)

                        currentBusEndPoint = listBusStations[1].id
                        endPointEdit.setText(listBusStations[1].name)
                    }
                }

            }

            val response2 = APIServiceImpl.busOperatorService().getBusOperators(token).awaitResponse()
            if(response2.isSuccessful){
                listBusOperators = response2.body()?.data as ArrayList<BusOperator>
                // push all bus operator to list
                listBusOperators.add(0, BusOperator("all", "", "", "All"))


            }
        }


        // onClickListener for departureDate
        departureDate.setOnClickListener {
            handleSelectDate()
        }

        departureDateEdit.setOnClickListener {
            handleSelectDate()
        }


        searchBus.setOnClickListener {
            val intent = Intent(this.requireContext(), BusSearch::class.java)
            val departureId = listBusStations.find { it.name == startPointEdit.text.toString() }?.id
            if(departureId == null){
                Toast.makeText(this.requireContext(), "Vui lòng chọn điểm đi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val destinationId = listBusStations.find { it.name == endPointEdit.text.toString() }?.id
            if(destinationId == null){
                Toast.makeText(this.requireContext(), "Vui lòng chọn điểm đến", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val inputDateString = departureDateEdit.text.toString()
            if(inputDateString == ""){
                Toast.makeText(this.requireContext(), "Vui lòng chọn ngày đi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val inputFormatter = DateTimeFormatter.ofPattern("M/d/yyyy")
            val localDate = LocalDate.parse(inputDateString, inputFormatter)
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val localDateTime = localDate.atStartOfDay()
            val zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
            val outputDateString = outputFormatter.format(zonedDateTime)
            val fromToString = startPointEdit.text.toString() + " -> " + endPointEdit.text.toString()
            intent.putExtra("departureId", departureId)
            intent.putExtra("destinationId", destinationId)
            intent.putExtra("outputDateString", outputDateString)
            intent.putExtra("fromToString", fromToString)
            startActivity(intent)
        }

        viewAllNews.setOnClickListener {
            replaceWithBlogFragment(requireContext() as AppCompatActivity, BlogFragment())
        }

        var blogPage = 1
        var blogLimit = 20
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
                                val intent = Intent(this@HomepageFragment.requireContext(), BlogDetailActivity::class.java)
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
    }

}