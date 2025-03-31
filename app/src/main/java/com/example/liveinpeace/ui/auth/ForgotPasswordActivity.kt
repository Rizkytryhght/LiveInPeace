package com.example.liveinpeace.ui.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.liveinpeace.R
import com.example.liveinpeace.viewModel.AuthViewModel

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val emailField = findViewById<EditText>(R.id.emailEditText)
        val resetButton = findViewById<Button>(R.id.resetButton)
        val backButton = findViewById<ImageView>(R.id.backButton)
        val loginPromptTextView = findViewById<TextView>(R.id.loginPromptTextView)

        loginPromptTextView.setOnClickListener {
            finish()
        }

        resetButton.setOnClickListener {
            val email = emailField.text.toString()

            if (email.isNotEmpty()) {
                viewModel.resetPassword(email) { success, message ->
                    if (success) {
                        Toast.makeText(this, "Cek email untuk reset password", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Email harus diisi!", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}
