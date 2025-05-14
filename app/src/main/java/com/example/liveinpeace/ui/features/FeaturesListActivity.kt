package com.example.liveinpeace.ui.features

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.liveinpeace.R
import com.example.liveinpeace.ui.checklist.ChecklistIbadahActivity
import com.example.liveinpeace.ui.home.HomeActivity
import com.example.liveinpeace.ui.note.NoteActivity
import com.example.liveinpeace.ui.profile.ProfileActivity
import com.example.liveinpeace.ui.reminder.ReminderActivity
import com.example.liveinpeace.ui.dass.DASSQuestionnaireActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class FeaturesListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feature_list)

        // Card Checklist Ibadah
        val cardChecklistIbadah = findViewById<CardView>(R.id.cardChecklistIbadah)
        cardChecklistIbadah.setOnClickListener {
            val intent = Intent(this, ChecklistIbadahActivity::class.java)
            startActivity(intent)
        }

        val reminderCard = findViewById<CardView>(R.id.reminderCard)
        reminderCard.setOnClickListener {
            val intent = Intent(this, ReminderActivity::class.java)
            startActivity(intent)
        }

        val cardDASSQuestionnaire = findViewById<CardView>(R.id.cardDASSQuestionnaire) // ID baru
        cardDASSQuestionnaire.setOnClickListener {
            val intent = Intent(this, DASSQuestionnaireActivity::class.java)
            startActivity(intent)
        }

        // Bottom Navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_features

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
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