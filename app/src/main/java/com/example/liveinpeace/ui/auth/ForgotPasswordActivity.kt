package com.example.liveinpeace.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.liveinpeace.R
import com.example.liveinpeace.viewmodel.AuthViewModel

//class ForgotPasswordActivity : AppCompatActivity() {
//    private lateinit var viewModel: AuthViewModel
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_forgot_password)
//
//        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
//
//        val emailField = findViewById<EditText>(R.id.emailEditText)
//        val resetButton = findViewById<Button>(R.id.resetButton) // Pastikan id ini ada di XML
//
//        resetButton.setOnClickListener {
//            val email = emailField.text.toString().trim()
//
//            if (email.isNotEmpty()) {
//                viewModel.sendPasswordReset(email) { success, message ->
//                    if (success) {
//                        Toast.makeText(this, "Email reset password telah dikirim!", Toast.LENGTH_SHORT).show()
//                        startActivity(Intent(this, LoginActivity::class.java))
//                        finish()
//                    } else {
//                        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            } else {
//                Toast.makeText(this, "Email harus diisi!", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}
