package com.example.kotlin.User.Screen.BottomNavigate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.kotlin.*
import com.example.kotlin.DataClass.HistoryItem
import com.example.kotlin.DataClass.SuccessMessage
import com.example.kotlin.utils.UserInformation
import com.example.kotlin.utils.ZaloPay.AppInfo
import com.example.kotlin.utils.ZaloPay.CreateOrder
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener

class BottomNavigation : AppCompatActivity() {
    private val homepageFragment = HomepageFragment()
    private val personalFragment = CaNhanFragment()
    private val blogFragment = BlogFragment()
    private val ticketFrament = TicketHistoryFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navigation)

        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX);

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

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
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        ZaloPaySDK.getInstance().onResult(intent)
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