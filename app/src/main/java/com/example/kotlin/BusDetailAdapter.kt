package com.example.kotlin

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kotlin.BusInformationFragment
import com.example.kotlin.BusOperatorFragment

class BusDetailAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        Log.d("test", "createFragment: $position")
        return when(position){
            0 -> BusOperatorFragment()
            1 -> BusInformationFragment()
            else -> BusOperatorFragment()
        }
    }
}