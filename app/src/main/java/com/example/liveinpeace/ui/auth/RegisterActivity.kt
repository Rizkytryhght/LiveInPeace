package com.example.liveinpeace.ui.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.liveinpeace.R
import com.example.liveinpeace.viewmodel.AuthViewModel

//class RegisterActivity : AppCompatActivity() {
//    private lateinit var viewModel: AuthViewModel
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_register)
//
//        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
//
//        val emailField = findViewById<EditText>(R.id.emailEditText)
//        val passwordField = findViewById<EditText>(R.id.passwordEditText)
//        val registerButton = findViewById<Button>(R.id.registerButton)
//
//        registerButton.setOnClickListener {
//            val email = emailField.text.toString()
//            val password = passwordField.text.toString()
//
//            if (email.isNotEmpty() && password.isNotEmpty()) {
//                viewModel.register(email, password) { success, message ->
//                    if (success) {
//                        Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
//                        finish() // Kembali ke login
//                    } else {
//                        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            } else {
//                Toast.makeText(this, "Email dan password harus diisi!", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}
