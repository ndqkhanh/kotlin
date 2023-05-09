package com.example.kotlin.Admin.Screen.BottomNavigation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.Toast
import com.example.kotlin.Admin.Screen.Bus.AdminBusActivity
import com.example.kotlin.Admin.Screen.BusOperator.AdminBusOperatorActivity
import com.example.kotlin.Admin.Screen.BusTicket.AdminBusTicketActivity
import com.example.kotlin.AdminGridAdapter
import com.example.kotlin.AdminItem
import com.example.kotlin.BlogManagementActivity
import com.example.kotlin.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdminFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminFragment : Fragment() {
    private lateinit var grid: GridView
    private lateinit var adapter: AdminGridAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_admin, container, false)
        var adminItemList = generateCompanyData() //implemened below
        grid = rootView.findViewById(R.id.adminGV)
        adapter = AdminGridAdapter(requireContext(), adminItemList)
        grid.adapter = adapter
        grid.setOnItemClickListener { adapterView, view, i, l ->
            when(adminItemList.get(i).id)
            {
                1 -> Intent(context, AdminBusActivity::class.java).also {
                    startActivity(it)
                }
                2 -> Intent(context, AdminBusTicketActivity::class.java).also {
                    startActivity(it)
                }
                3-> Intent(context, AdminBusOperatorActivity::class.java).also {
                    startActivity(it)
                }
                4 -> Intent(context, BlogManagementActivity::class.java).also {
                    startActivity(it)
                }
            }
            Toast.makeText(
                context, " Selected Company is " + adminItemList.get(i).name,

                Toast.LENGTH_SHORT
            ).show()
        }
        return rootView
    }

    private fun generateCompanyData(): ArrayList<AdminItem> {
        var result = ArrayList<AdminItem>()
        var adminItem: AdminItem = AdminItem()
        adminItem.id = 1
        adminItem.name = "Chuyến xe"
        adminItem.image = R.drawable.bus_icon // CHANGE LATER
        result.add(adminItem)

        adminItem = AdminItem()
        adminItem.id = 2
        adminItem.name = "Đặt chỗ"
        adminItem.image = R.drawable.ticket_icon // CHANGE LATER
        result.add(adminItem)

        adminItem = AdminItem()
        adminItem.id = 3
        adminItem.name = "Nhà xe"
        adminItem.image = R.drawable.bus_operator_icon // CHANGE LATER
        result.add(adminItem)

        adminItem = AdminItem()
        adminItem.id = 4
        adminItem.name = "Bài viết"
        adminItem.image = R.drawable.blog_icon // CHANGE LATER
        result.add(adminItem)

        return result
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdminFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}