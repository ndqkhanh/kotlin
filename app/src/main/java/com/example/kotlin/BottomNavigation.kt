package com.example.kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.kotlin.databinding.ActivityBottomNavigationBinding
import com.example.kotlin.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class BottomNavigation : AppCompatActivity() {
    lateinit var binding: ActivityBottomNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HomepageFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.search -> replaceFragment(HomepageFragment())
                R.id.blog -> replaceFragment(BlogFragment())
                R.id.ticket -> replaceFragment(PersonalInformationFragment())
                R.id.user -> replaceFragment(CaNhanFragment())
                else ->{



                }

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

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()


    }



}