package com.example.liveinpeace.ui.checklist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.liveinpeace.ui.theme.LiveInPeaceTheme

class ChecklistIbadahActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiveInPeaceTheme {
                ChecklistIbadahScreen()
            }
        }
    }
}