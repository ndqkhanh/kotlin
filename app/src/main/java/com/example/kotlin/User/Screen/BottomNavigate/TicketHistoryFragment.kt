package com.example.kotlin.User.Screen.BottomNavigate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TabHost
import android.widget.TabHost.TabSpec
import androidx.fragment.app.Fragment
import com.example.kotlin.R

class TicketHistoryFragment : Fragment() {
    private lateinit var tabHost: TabHost
    private lateinit var tabSpec1: TabSpec
    private lateinit var tabSpec2: TabSpec
    private lateinit var tabSpec3: TabSpec

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_ticket_history, container, false)
        tabHost = root.findViewById(R.id.tabHostVe)
        tabHost.setup()

        tabSpec1 = tabHost.newTabSpec("t1")
        tabSpec1.setContent(R.id.tab1)
        tabSpec1.setIndicator("Vé hiện tại")
        tabHost.addTab(tabSpec1)

        tabSpec2 = tabHost.newTabSpec("t2")
        tabSpec2.setContent(R.id.tab2)
        tabSpec2.setIndicator("Vé đã đi")
        tabHost.addTab(tabSpec2)

        tabSpec3 = tabHost.newTabSpec("t3")
        tabSpec3.setContent(R.id.tab3)
        tabSpec3.setIndicator("Vé đã hủy")
        tabHost.addTab(tabSpec3)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}