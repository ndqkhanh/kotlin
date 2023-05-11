package com.example.kotlin.User.Screen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TabHost
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.adapters.TabHostBindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.kotlin.*
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class BusSearch : AppCompatActivity() {
    var listBuses = ArrayList<Bus>()
    lateinit var rv_bus_search: RecyclerView
    val retrofit = APIServiceImpl()
    lateinit var listBusOperators: ArrayList<BusOperator>


    // fun short string to limit characters
    private fun String.short(limit: Int = 10): String {
        return if (this.length > limit) {
            this.substring(0, limit) + "..."
        } else {
            this
        }
    }
    class CustomItem(private val items: List<Bus>): RecyclerView.Adapter<CustomItem.ViewHolder>() {
        var onItemClick: ((String) -> Unit)? = null

        inner class ViewHolder(listItemView: View): RecyclerView.ViewHolder(listItemView) {
//            val itemName = listItemView.findViewById(R.id.itemName) as TextView
            val busTimeStartText = listItemView.findViewById(R.id.busTimeStartText) as TextView
            val busTimeEndText = listItemView.findViewById(R.id.busTimeEndText) as TextView
            val busDurationText = listItemView.findViewById(R.id.busDurationText) as TextView
            val busStartPointText = listItemView.findViewById(R.id.busStartPointText) as TextView
            val busEndPointText = listItemView.findViewById(R.id.busEndPointText) as TextView
            val busPricing = listItemView.findViewById(R.id.busPricing) as TextView
            val busNumOfLeftSeats = listItemView.findViewById(R.id.busNumOfLeftSeats) as TextView
            val busItemOperatorThumbnail = listItemView.findViewById(R.id.busItemOperatorThumbnail) as ImageView
            val busItemOperatorName = listItemView.findViewById(R.id.busItemOperatorName) as TextView
            val busItemOperatorType = listItemView.findViewById(R.id.busItemOperatorType) as TextView
            val busItemOperatorRating = listItemView.findViewById(R.id.busItemOperatorRating) as TextView
            val bookButton = listItemView.findViewById(R.id.buyButton) as Button
            val thisItem = listItemView.findViewById<ConstraintLayout>(R.id.bus_search_item)
            init {
                thisItem.setOnClickListener {
                    onItemClick?.invoke(items[adapterPosition].bo_id)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val context = parent.context
            val inflater = LayoutInflater.from(context)
            val studentView = inflater.inflate(R.layout.fragment_bus_search_item, parent, false)
            return ViewHolder(studentView)
        }

        override fun getItemCount(): Int {
            return items.size
        }



        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.busTimeStartText.text = items[position].start_time_hour
            holder.busTimeEndText.text = items[position].end_time_hour
            holder.busDurationText.text = items[position].duration
            holder.busStartPointText.text = items[position].start_point.name
            holder.busEndPointText.text = items[position].end_point.name
            holder.busPricing.text = items[position].pricing_format + "đ"
            holder.busNumOfLeftSeats.text = items[position].num_of_seats.toString() + " chỗ trống"
            holder.busItemOperatorName.text = items[position].bus_operators.name
            holder.busItemOperatorType.text = if(items[position].type == 0) "Xe hạng sang" else if(items[position].type == 1) "Xe ghế ngồi" else "Xe giường nằm"
            holder.busItemOperatorRating.text = items[position].rating.toString()

            Glide.with(holder.itemView.context)
                .load(items[position].bus_operators.image_url)
                .into(holder.busItemOperatorThumbnail)

            holder.bookButton.setOnClickListener {
                // Intent to ChoosePickUpLocationActivity
                val intent = Intent(holder.itemView.context, ChooseSeatActivity::class.java)
                intent.putExtra("busId", items[position].id)
                startActivity(holder.itemView.context, intent, null)
            }
        }

        // loadingHolder

    }
    var currentBusType: String = ""
    var currentBusOperator: String? = ""
    var currentBusPricing: Int = 0
    var countMax: Int = 0
    private lateinit var departureId: String
    private lateinit var destinationId: String
    private lateinit var outputDateString: String
    private lateinit var loading: LottieAnimationView
    private lateinit var busAdapter: CustomItem
    private lateinit var busDetailDialog: BottomSheetDialog
    private fun loadMoreResult(page: Int = 0, limit: Int = 10){
        if(page == 0) loading.visibility = View.VISIBLE

        val busOperatorId = if(currentBusOperator == "") null else currentBusOperator
        val pricing = currentBusPricing
        var typeOfSeatValue = if(currentBusType == "") null else currentBusType.toInt()
        Log.d("Search", "page: $page - $limit - $busOperatorId - $pricing - $typeOfSeatValue")

        Log.d("Search", "$departureId - $destinationId - $outputDateString")
        GlobalScope.launch (Dispatchers.IO) {
            val response = APIServiceImpl().searchBusses().search(BusSearchRequest(departureId, destinationId, page, limit, outputDateString, pricing, typeOfSeatValue, busOperatorId)).awaitResponse()
            Log.d("Search", response.toString())
            if(response.isSuccessful){
                var data = response.body()?.data as ArrayList<Bus>
                if(page == 0)
                    listBuses = data
                else
                    listBuses.addAll(data)
                withContext(Dispatchers.Main) {

                    if(page == 0){
                        busAdapter = CustomItem(listBuses)
                        rv_bus_search!!.adapter = busAdapter
                        val openDetailDialog: ((String)->Unit)? = {
                            val viewDetail = Intent(this@BusSearch, ChiTietChuyenXe::class.java)
                            viewDetail.putExtra("boId", it)
                            startActivity(viewDetail)
                        }
                        busAdapter.onItemClick = openDetailDialog
                    }

                    busAdapter.notifyItemRangeInserted(busAdapter.itemCount, listBuses.size - 1)
                    if(page == 0) loading.visibility = View.GONE


                }
            }
        }
    }

    var currentPage = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_search)
        loading = findViewById(R.id.loading)
        // load json to loading
//        val resources = resources.getIdentifier("bus-loader", "raw", packageName)
//        loading.setAnimation(resources)
//        loading.playAnimation()
//        loading.loop(true)
        departureId = intent.getStringExtra("departureId") ?: ""
        destinationId = intent.getStringExtra("destinationId") ?: ""
        outputDateString = intent.getStringExtra("outputDateString") ?: ""
        val fromToStringText = intent.getStringExtra("fromToString") ?: ""
        val fromToString = findViewById<TextView>(R.id.fromToString)
        fromToString.text = fromToStringText
        val dateString = findViewById<TextView>(R.id.dateString)
        dateString.text = outputDateString
        val backButtonScreen = findViewById<ImageView>(R.id.backButtonScreen)
        backButtonScreen.setOnClickListener {
            finish()
        }
        rv_bus_search = findViewById(R.id.rv_bus_search)
        rv_bus_search.layoutManager = LinearLayoutManager(this)
        loadMoreResult()

        rv_bus_search.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    currentPage++
                    Log.d("test2", currentPage.toString())
                    loadMoreResult(currentPage)
                }
            }


        })


        val filterBusType = findViewById<Button>(R.id.filterBusType)
        filterBusType.setOnClickListener {
            FragmentOperatorFilter().apply {
                var listBusType = ArrayList<ListItemFormat>()
                listBusType.add(ListItemFormat("0", "Hạng sang"))
                listBusType.add(ListItemFormat("1", "Thường"))
                listBusType.add(ListItemFormat("2", "Giường"))

                // pass listBusOperators to FragmentOperatorFilter
                arguments = Bundle().apply {
                    putSerializable("listFilter", listBusType)
                    putString("titleName", "Chọn loại xe")

                    if(currentBusType != ""){
                        putString("defaultId", currentBusType)
                    }
                }
                show(supportFragmentManager, FragmentOperatorFilter.TAG)

                // onItemClick with data
                onItemClick = onItemClick@{item ->
                    if(item.id == ""){
                        currentBusType = ""
                        filterBusType.text = "LOẠI XE"
                        filterBusType.backgroundTintList = getColorStateList(R.color.white)

                        loadMoreResult()
                        return@onItemClick
                    }
                    currentBusType = item.id
                    filterBusType.text = item.name.short(7)
                    filterBusType.backgroundTintList = getColorStateList(R.color.teal_200)

                    loadMoreResult()
                }
            }
        }

        val filterBusOperator = findViewById<Button>(R.id.filterBusOperator)
        filterBusOperator.setOnClickListener {
            FragmentOperatorFilter().apply {
                // convert listBusOperators to listBusOperator
                var listBusOperator = ArrayList<ListItemFormat>()
                listBusOperators.forEach {
                    listBusOperator.add(ListItemFormat(it.id, it.name))
                }

                // pass listBusOperators to FragmentOperatorFilter
                arguments = Bundle().apply {
                    putSerializable("listFilter", listBusOperator)
                    putString("titleName", "Chọn nhà xe")

                    if(currentBusOperator != ""){
                        putString("defaultId", currentBusOperator)
                    }
                }
                show(supportFragmentManager, FragmentOperatorFilter.TAG)

                // onItemClick with data
                onItemClick = onItemClick@{ item ->
                    if(item.id == ""){
                        currentBusOperator = ""
                        filterBusOperator.text = "NHÀ XE"
                        filterBusOperator.backgroundTintList = getColorStateList(R.color.white)

                        loadMoreResult()
                        return@onItemClick
                    }
                    currentBusOperator = item.id

                    filterBusOperator.text = item.name.short(7)
                    filterBusOperator.backgroundTintList = getColorStateList(R.color.teal_200)
                    loadMoreResult()
                }
            }
        }

        val filterBusPricing = findViewById<Button>(R.id.filterBusPricing)
        filterBusPricing.setOnClickListener {
            PricingFilter().apply {
                arguments = Bundle().apply {
                    if(currentBusPricing != 0){
                        putInt("defaultPricing", currentBusPricing)
                    }
                }
                show(supportFragmentManager, "PricingFilter")
                onApply = onApply@{ pricing ->
                    if(pricing == 0){
                        currentBusPricing = 0
                        filterBusPricing.text = "GIÁ"
                        filterBusPricing.backgroundTintList = getColorStateList(R.color.white)

                        loadMoreResult()
                        return@onApply
                    }
                    currentBusPricing = pricing
                    filterBusPricing.text = thousandToDecimalFormat(pricing).short(7)
                    filterBusPricing.backgroundTintList = getColorStateList(R.color.teal_200)
                    loadMoreResult()
                }
            }
        }

        GlobalScope.launch (Dispatchers.IO) {
            val response2 = APIServiceImpl.busOperatorService().getBusOperators().awaitResponse()
            if(response2.isSuccessful){
                listBusOperators = response2.body()?.data as ArrayList<BusOperator>
                // push all bus operator to list
                listBusOperators.add(0, BusOperator("all", "", "", "All"))

            }
        }
    }


}
