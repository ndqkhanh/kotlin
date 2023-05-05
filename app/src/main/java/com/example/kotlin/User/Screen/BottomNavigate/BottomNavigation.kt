package com.example.kotlin.User.Screen.BottomNavigate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.kotlin.*
import com.example.kotlin.User.Screen.BottomNavigate.TicketHistory.TicketHistoryFragment
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
    }

    private fun replaceFragment(fragment : Fragment){
        if(fragment != null){
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_layout,fragment)
            fragmentTransaction.commit()
        }
    }

    companion object {
        @JvmStatic
        fun replaceWithBlogFragment(activity: AppCompatActivity, fragment: Fragment) {
            // set bottom navigation to blog
            val bottomNavView = activity.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavView.selectedItemId = R.id.blog
        }
    }

}