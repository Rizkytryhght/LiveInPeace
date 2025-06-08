package com.example.liveinpeace.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.liveinpeace.MainActivity
import com.example.liveinpeace.ui.theme.LiveInPeaceTheme

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiveInPeaceTheme {
                LoginScreen(
                    onLoginSuccess = {
                        // Pindah ke MainActivity setelah login sukses
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    onNavigateToRegister = {
                        // Pindah ke RegisterActivity (belum kita buat Compose-nya)
                        startActivity(Intent(this, RegisterActivity::class.java))
                    },
                    onForgotPassword = {
                        // Pindah ke ForgotPasswordActivity (jika ada)
                        startActivity(Intent(this, ForgotPasswordActivity::class.java))
                    }
                )
            }
        }
    }
}