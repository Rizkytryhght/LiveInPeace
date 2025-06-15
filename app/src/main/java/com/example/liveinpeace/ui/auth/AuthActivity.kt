package com.example.liveinpeace.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.liveinpeace.MainActivity
import com.example.liveinpeace.ui.theme.LiveInPeaceTheme
import com.example.liveinpeace.viewModel.AuthViewModel

class AuthActivity : ComponentActivity() {
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Check if user is already logged in
        if (authViewModel.isUserLoggedIn()) {
            // User sudah login, langsung ke MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContent {
            LiveInPeaceTheme {
                LoginScreen(
                    onLoginSuccess = {
                        // Pindah ke MainActivity setelah login sukses
                        startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                        finish()
                    },
                    onNavigateToRegister = {
                        // Pindah ke RegisterActivity
                        startActivity(Intent(this@AuthActivity, RegisterActivity::class.java))
                    },
                    onForgotPassword = {
                        // Pindah ke ForgotPasswordActivity
                        startActivity(Intent(this@AuthActivity, ForgotPasswordActivity::class.java))
                    }
                )
            }
        }
    }
}

//import android.content.Intent
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.lifecycle.ViewModelProvider
//import com.example.liveinpeace.MainActivity
//import com.example.liveinpeace.ui.theme.LiveInPeaceTheme
//import com.example.liveinpeace.viewModel.AuthViewModel
//
//class AuthActivity : ComponentActivity() {
//    private lateinit var authViewModel: AuthViewModel
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            LiveInPeaceTheme {
//                LoginScreen(
//                    onLoginSuccess = {
//                        // Pindah ke MainActivity setelah login sukses
//                        startActivity(Intent(this, MainActivity::class.java))
//                        finish()
//                    },
//                    onNavigateToRegister = {
//                        // Pindah ke RegisterActivity (belum kita buat Compose-nya)
//                        startActivity(Intent(this, RegisterActivity::class.java))
//                    },
//                    onForgotPassword = {
//                        // Pindah ke ForgotPasswordActivity (jika ada)
//                        startActivity(Intent(this, ForgotPasswordActivity::class.java))
//                    }
//                )
//            }
//        }
//    }
//}