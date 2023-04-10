package com.example.kotlin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.kotlin.R

class BusOperatorFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("test", "3")

        return inflater.inflate(R.layout.fragment_bus_operator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
        val btnSubmit = view.findViewById<AppCompatButton>(R.id.btnSubmit)
        Log.d("test", "2")
        btnSubmit.setOnClickListener {
            Log.i("BusOperatorFragment", "Button clicked")
        }
    }
}