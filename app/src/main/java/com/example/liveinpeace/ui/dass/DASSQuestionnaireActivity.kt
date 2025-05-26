package com.example.liveinpeace.ui.dass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

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
    NavHost(navController, startDestination = "dass_options") {
        composable("dass_options") { DASSOptionsScreen(navController) }
        composable("dass_introduction") { DASSIntroductionScreen(navController) }
        composable("dass_questionnaire") { DASSQuestionnaireScreen(navController) }
        composable("dass_history") { DASSHistoryScreen(navController) }
        composable(
            route = "dass_result/{answers}",
            arguments = listOf(navArgument("answers") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { backStackEntry ->
            val answersString = backStackEntry.arguments?.getString("answers") ?: ""
            DASSResultScreen(navController, answersString)
        }
    }
}