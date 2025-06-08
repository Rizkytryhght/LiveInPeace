package com.example.liveinpeace.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.liveinpeace.ui.theme.LiveInPeaceTheme

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LiveInPeaceTheme {
                RegisterScreen(
                    onBackClick = {
                        startActivity(Intent(this, AuthActivity::class.java))
                        finish()
                    },
                    onRegisterSuccess = {
                        finish()
                    }
                )
            }
        }
    }
}