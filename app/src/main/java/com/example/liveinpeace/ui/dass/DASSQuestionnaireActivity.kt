package com.example.liveinpeace.ui.dass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable

class DASSQuestionnaireActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    DASSNavHost()
                }
            }
        }
    }
}

@Composable
fun DASSNavHost() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "dass_introduction") {
        composable("dass_introduction") { DASSIntroductionScreen(navController) }
        composable("dass_questionnaire") { DASSQuestionnaireScreen(navController) }
        composable("dass_result") { DASSResultScreen(navController) }
    }
}