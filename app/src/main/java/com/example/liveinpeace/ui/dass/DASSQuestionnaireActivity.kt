package com.example.liveinpeace.ui.dass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class DASSQuestionnaireActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DASSNavHost()
        }
    }
}

@Composable
fun DASSNavHost() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "dass_introduction") {
        composable("dass_introduction") { DASSIntroductionScreen(navController) }
        composable("dass_questionnaire") { DASSQuestionnaireScreen() }
    }
}