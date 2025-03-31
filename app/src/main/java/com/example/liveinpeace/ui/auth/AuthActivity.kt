package com.example.liveinpeace.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.liveinpeace.MainActivity
import com.example.liveinpeace.R
import com.example.liveinpeace.viewmodel.AuthViewModel

class AuthActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        val emailField = findViewById<EditText>(R.id.emailEditText)
        val passwordField = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpTextView = findViewById<TextView>(R.id.signUpTextView)

        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password) { success, message ->
                    if (success) {
                        Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Email dan password harus diisi!", Toast.LENGTH_SHORT).show()
            }
        }

        signUpTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
