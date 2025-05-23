package com.example.liveinpeace.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.liveinpeace.R
<<<<<<< HEAD
import com.example.liveinpeace.ui.features.FeaturesListActivity
=======
import com.example.liveinpeace.ui.dashboard.DashboardActivity
import com.example.liveinpeace.ui.features.FeatureListActivity
>>>>>>> ed972b72df6b20771bd2d91a94b83da3970d14b7
import com.example.liveinpeace.ui.note.NoteActivity
import com.example.liveinpeace.ui.profile.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        if (bottomNavigationView == null) {
            Log.e("HomeActivity", "BottomNavigationView not found!")
            return
        }

        try {
            bottomNavigationView.selectedItemId = R.id.nav_home

            bottomNavigationView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_home -> {
                        true // Stay on HomeActivity
                    }
                    R.id.nav_notes -> {
                        startActivity(Intent(this, NoteActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                        true
                    }
                    R.id.nav_features -> {
                        startActivity(Intent(this, DashboardActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                        true
                    }
                    R.id.nav_profile -> {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                        true
                    }
                    else -> false
                }
<<<<<<< HEAD
                R.id.nav_features -> {
                    startActivity(Intent(this, FeaturesListActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
=======
>>>>>>> ed972b72df6b20771bd2d91a94b83da3970d14b7
            }
        } catch (e: Exception) {
            Log.e("HomeActivity", "Error setting up BottomNavigationView: ${e.message}")
            false
        }
    }
}

//import android.content.Intent
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.example.liveinpeace.R
//import com.example.liveinpeace.ui.dashboard.DashboardActivity
//import com.example.liveinpeace.ui.features.FeatureListActivity
//import com.example.liveinpeace.ui.note.NoteActivity
//import com.example.liveinpeace.ui.profile.ProfileActivity
//import com.google.android.material.bottomnavigation.BottomNavigationView
//
//class HomeActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_home)
//
//        startActivity(Intent(this, DashboardActivity::class.java))
//        finish()
//
//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
//        bottomNavigationView.selectedItemId = R.id.nav_home
//
//        bottomNavigationView.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.nav_home -> {
//                    true
//                }
//                R.id.nav_notes -> {
//                    startActivity(Intent(this, NoteActivity::class.java))
//                    overridePendingTransition(0, 0)
//                    finish()
//                    true
//                }
//                R.id.nav_features -> {
//                    startActivity(Intent(this, FeatureListActivity::class.java))
//                    overridePendingTransition(0, 0)
//                    finish()
//                    true
//                }
//                R.id.nav_profile -> {
//                    startActivity(Intent(this, ProfileActivity::class.java))
//                    overridePendingTransition(0, 0)
//                    finish()
//                    true
//                }
//                else -> false
//            }
//        }
//    }
//}