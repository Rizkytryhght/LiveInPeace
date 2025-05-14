package com.example.liveinpeace.ui.dass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class DASSQuestionnaireActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DASSQuestionnaireScreen() // Panggil composable dari file terpisah
        }
    }
}