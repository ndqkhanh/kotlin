package com.example.kotlin.Admin.Screen.BottomNavigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.kotlin.R
import com.example.kotlin.User.Screen.BottomNavigate.CaNhanFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomAdminNavigation : AppCompatActivity() {
    private val adminFragment = AdminFragment()
    private val caNhanFragment = CaNhanFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_admin_navigation)


        replaceFragment(adminFragment)
        var bottomNavView = findViewById<BottomNavigationView>(R.id.amdin_bottomNavigationView)
        bottomNavView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.dashboard -> replaceFragment(adminFragment)
                R.id.user -> replaceFragment(caNhanFragment)

                else ->{

                }

            }
            true
        }
    }

    private fun replaceFragment(fragment : Fragment){
        if(fragment != null){
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.admin_frame_layout,fragment)
            fragmentTransaction.commit()
        }
    }
}