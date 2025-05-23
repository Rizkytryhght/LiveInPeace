package com.example.liveinpeace.ui.dass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
        composable(
            route = "dass_result/{answers}",
            arguments = listOf(navArgument("answers") {
                type = NavType.StringType
                defaultValue = "" // Tambahkan default value untuk mencegah null
            })
        ) { backStackEntry ->
            val answersString = backStackEntry.arguments?.getString("answers") ?: ""
            DASSResultScreen(navController, answersString)
        }
    }
}