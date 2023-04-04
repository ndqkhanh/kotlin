package com.example.kotlin

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlin.R
import java.util.*

class BusInformationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("test", "3")

        return inflater.inflate(R.layout.fragment_bus_information, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//
//        arguments?.getBoolean("Test")?.let {
//            Log.d("Search", "haha $it")
//        }
//        arguments?.getStringArray("Test2")?.let {
//            Log.d("Search", "lala " + it.toString())
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPreference = activity?.getSharedPreferences("BusDetail", Context.MODE_PRIVATE)
        val busInformation = sharedPreference?.getString("busInformation", null)
        val splits = busInformation?.split(",")

        val busInfoRV = view.findViewById<RecyclerView>(R.id.busInfoRV)

        val busInfoAdapter = CustomBusDataItem(splits as ArrayList<String>)
        busInfoRV!!.adapter = busInfoAdapter

        busInfoRV!!.layoutManager = GridLayoutManager(context, 2)

        val busAvatar = sharedPreference?.getString("busAvatar", null)
        val imageView4 = view.findViewById<ImageView>(R.id.imageView4)
        Glide.with(imageView4.context)
            .load(busAvatar)
            .into(imageView4)
    }
}