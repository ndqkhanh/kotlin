package com.example.kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView


class BottomNavigation : AppCompatActivity() {
    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navigation)

        // Initialize and assign variable
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        // Set Home selected
        bottomNavigationView.selectedItemId = R.id.search
        // Perform item selected listener
        var intent: Intent
        bottomNavigationView.setOnNavigationItemSelectedListener{ menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    finish()
                    intent = Intent(this, HomePage::class.java)
                    startActivity(intent)
                    true
                }

                R.id.blog -> {
                    finish()
                    intent = Intent(this, BlogSeeAllActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.ticket -> {
                    finish()
                    intent = Intent(this, PersonalInformation::class.java)
                    startActivity(intent)
                    true
                }

                R.id.user -> {
                    finish()
                    intent = Intent(this, CaNhanActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }


        }

    }



}