package com.example.liveinpeace.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.liveinpeace.R
import com.example.liveinpeace.ui.dashboard.DashboardActivity
import com.example.liveinpeace.ui.features.FeaturesListActivity
import com.example.liveinpeace.ui.note.NoteActivity
import com.example.liveinpeace.ui.profile.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        if (bottomNavigationView == null) {
            Log.e("HomeActivity", "BottomNavigationView not found in layout!")
            return
        }

        try {
            bottomNavigationView.selectedItemId = R.id.nav_home

            bottomNavigationView.setOnItemSelectedListener { item ->
                Log.d("HomeActivity", "Nav item selected: ${item.itemId}")
                when (item.itemId) {
                    R.id.nav_home -> {
                        Log.d("HomeActivity", "Staying on HomeActivity")
                        true
                    }
                    R.id.nav_notes -> {
                        Log.d("HomeActivity", "Navigating to NoteActivity")
                        startActivity(Intent(this, NoteActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                        true
                    }
                    R.id.nav_features -> {
                        Log.d("HomeActivity", "Navigating to FeaturesListActivity")
//                        startActivity(Intent(this, DashboardActivity::class.java))
                        // Alternatif: FeaturesListActivity (dari HEAD)
                         startActivity(Intent(this, FeaturesListActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                        true
                    }
                    R.id.nav_profile -> {
                        Log.d("HomeActivity", "Navigating to ProfileActivity")
                        startActivity(Intent(this, ProfileActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                        true
                    }
                    else -> {
                        Log.w("HomeActivity", "Unknown nav item: ${item.itemId}")
                        false
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("HomeActivity", "Error setting up BottomNavigationView: ${e.message}")
        }
    }
}