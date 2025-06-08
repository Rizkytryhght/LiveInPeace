
package com.example.liveinpeace.ui.features

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.liveinpeace.ui.checklist.ChecklistIbadahActivity
import com.example.liveinpeace.ui.note.NoteActivity
import com.example.liveinpeace.ui.profile.ProfileActivity
import com.example.liveinpeace.ui.reminder.ReminderActivity
import com.example.liveinpeace.ui.dass.DASSQuestionnaireActivity
import com.example.liveinpeace.ui.mood.MoodTrackerActivity

@Suppress("DEPRECATION")
class FeaturesListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FeaturesScreen(
                onNavigate = { route ->
                    when (route) {
                        "notes" -> {
                            startActivity(Intent(this@FeaturesListActivity, NoteActivity::class.java))
                            overridePendingTransition(0, 0)
                            finish()
                        }
                        "profile" -> {
                            startActivity(Intent(this@FeaturesListActivity, ProfileActivity::class.java))
                            overridePendingTransition(0, 0)
                            finish()
                        }
                        "features" -> {
                            // Stay in current activity
                        }
                    }
                },
                currentRoute = "features",
                onFeatureClick = { feature ->
                    when (feature) {
                        "checklist" -> {
                            startActivity(Intent(this@FeaturesListActivity, ChecklistIbadahActivity::class.java))
                        }
                        "reminder" -> {
                            startActivity(Intent(this@FeaturesListActivity, ReminderActivity::class.java))
                        }
                        "dass" -> {
                            startActivity(Intent(this@FeaturesListActivity, DASSQuestionnaireActivity::class.java))
                        }
                        "mood" -> {
                            startActivity(Intent(this@FeaturesListActivity, MoodTrackerActivity::class.java))
                        }
                    }
                }
            )
        }
    }
}