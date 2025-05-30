package com.example.liveinpeace.ui.mood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.liveinpeace.ui.theme.LiveInPeaceTheme

class MoodTrackerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiveInPeaceTheme {
                MoodTrackerNavHost()
            }
        }
    }
}

@Composable
fun MoodTrackerNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "mood_tracker") {
        composable("mood_tracker") {
            MoodTrackerScreen(navController)
        }
    }
}