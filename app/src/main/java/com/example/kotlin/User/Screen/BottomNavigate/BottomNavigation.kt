package com.example.kotlin.User.Screen.BottomNavigate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.kotlin.*
import com.google.android.material.bottomnavigation.BottomNavigationView


class BottomNavigation : AppCompatActivity() {
    private val homepageFragment = HomepageFragment()
    private val personalFragment = CaNhanFragment()
    private val blogFragment = BlogFragment()
    private val ticketFrament = TicketHistoryFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navigation)

        replaceFragment(homepageFragment)

        var bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.search -> replaceFragment(homepageFragment)
                R.id.blog -> replaceFragment(blogFragment)
                R.id.ticket -> replaceFragment(ticketFrament)
                R.id.user -> replaceFragment(personalFragment)
            }
            true
        }



//        setContentView(R.layout.activity_bottom_navigation)

//        // Initialize and assign variable
//        bottomNavigationView = findViewById(R.id.bottomNavigationView)
//        // Set Home selected
//        bottomNavigationView.selectedItemId = R.id.search
//        // Perform item selected listener
//        var intent: Intent
//        bottomNavigationView.setOnNavigationItemSelectedListener{ menuItem ->
//            when (menuItem.itemId) {
//                R.id.search -> {
//                    finish()
//                    intent = Intent(this, HomePage::class.java)
//                    startActivity(intent)
//                    true
//                }
//
//                R.id.blog -> {
//                    finish()
//                    intent = Intent(this, BlogSeeAllActivity::class.java)
//                    startActivity(intent)
//                    true
//                }
//
//                R.id.ticket -> {
//                    finish()
//                    intent = Intent(this, PersonalInformation::class.java)
//                    startActivity(intent)
//                    true
//                }
//
//                R.id.user -> {
//                    finish()
//                    intent = Intent(this, CaNhanActivity::class.java)
//                    startActivity(intent)
//                    true
//                }
//
//                else -> false
//            }
//
//
//        }

    }

    private fun replaceFragment(fragment : Fragment){
        if(fragment != null){
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_layout,fragment)
            fragmentTransaction.commit()
        }
    }



}