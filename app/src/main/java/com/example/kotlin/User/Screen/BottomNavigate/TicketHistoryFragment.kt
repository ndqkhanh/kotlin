package com.example.kotlin.User.Screen.BottomNavigate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kotlin.R

/**
 * A simple [Fragment] subclass.
 * Use the [TicketHistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TicketHistoryFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ticket_history, container, false)
    }


}