package com.example.kotlin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentOperatorFilter.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentOperatorFilter : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var onItemClick: ((ListItemFormat) -> Unit)? = null
    lateinit var defaultId: String
    lateinit var listFilter: java.util.ArrayList<ListItemFormat>
    var titleName = "Chọn nơi xuất phát"
    class CustomItem(private val items: List<ListItemFormat>, private val defaultId: String): RecyclerView.Adapter<CustomItem.ViewHolder>() {
        var onItemClick: ((ListItemFormat) -> Unit)? = null

        inner class ViewHolder(listItemView: View): RecyclerView.ViewHolder(listItemView) {
            val itemName = listItemView.findViewById(R.id.itemName) as TextView
            val radioButtonSelect = listItemView.findViewById(R.id.radioButtonSelect) as RadioButton
            init {
                listItemView.setOnClickListener {
                    onItemClick?.invoke(items[adapterPosition])
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val context = parent.context
            val inflater = LayoutInflater.from(context)
            val studentView = inflater.inflate(R.layout.fragment_operator_filter_item, parent, false)
            return ViewHolder(studentView)
        }

        override fun getItemCount(): Int {
            return items.size
        }


        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.itemName.text = items[position].name
            holder.radioButtonSelect.isChecked = defaultId == items[position].id
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            listFilter = it.getSerializable("listFilter") as ArrayList<ListItemFormat>
            titleName = it.getString("titleName") ?: "Chọn nơi xuất phát"
            defaultId = it.getString("defaultId") ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_operator_filter, container, false)
        // Inflate the layout for this fragment

        val filterList = view.findViewById<RecyclerView>(R.id.filterList)
        // convert listFilter to list of names
        var itemsAdapter = CustomItem(listFilter, defaultId)
        itemsAdapter.onItemClick = {item ->
            onItemClick?.invoke(item)
            dismiss()
        }
        filterList.adapter = itemsAdapter
        filterList.layoutManager = LinearLayoutManager(context)

        val filterTitle = view?.findViewById<TextView>(R.id.filterTitle)
        filterTitle?.text = titleName

        val closeFilter = view.findViewById<TextView>(R.id.closeFilter)

        closeFilter.setOnClickListener {
            dismiss()
        }

        val removeFilter = view.findViewById<TextView>(R.id.removeFilter)

        removeFilter.setOnClickListener {
            onItemClick?.invoke(ListItemFormat("", ""))
            dismiss()
        }

        val searchEditText = view.findViewById<TextView>(R.id.searchEditText)

        // onType
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filteredList = listFilter.filter { it.name.split(" ")[0].contains(s.toString()) }
                itemsAdapter = CustomItem(filteredList, defaultId)

                itemsAdapter.onItemClick = {item ->
                    onItemClick?.invoke(item)
                    dismiss()
                }

                filterList.adapter = itemsAdapter

                itemsAdapter.notifyDataSetChanged()

            }
        })

        return view
    }

    companion object {
        const val TAG = "FragmentOperatorFilter"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentOperatorFilter.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentOperatorFilter().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}