package com.example.liveinpeace.ui.features

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.liveinpeace.R
import com.example.liveinpeace.ui.checklist.ChecklistIbadahActivity
import com.example.liveinpeace.ui.note.NoteActivity
import com.example.liveinpeace.ui.profile.ProfileActivity
import com.example.liveinpeace.ui.reminder.ReminderActivity
import com.example.liveinpeace.ui.dass.DASSQuestionnaireActivity
import com.example.liveinpeace.ui.mood.MoodTrackerActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class FeaturesListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feature_list)

        // Setup kartu
        findViewById<CardView>(R.id.cardChecklistIbadah).setOnClickListener {
            startActivity(Intent(this, ChecklistIbadahActivity::class.java))
        }
        findViewById<CardView>(R.id.reminderCard).setOnClickListener {
            startActivity(Intent(this, ReminderActivity::class.java))
        }
        findViewById<CardView>(R.id.cardDASSQuestionnaire).setOnClickListener {
            startActivity(Intent(this, DASSQuestionnaireActivity::class.java))
        }
        findViewById<CardView>(R.id.cardMoodTracker).setOnClickListener {
            startActivity(Intent(this, MoodTrackerActivity::class.java))
        }

        // Setup bottom navbar
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_features
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_notes -> {
                    startActivity(Intent(this, NoteActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_features -> true
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}