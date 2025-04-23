package com.example.liveinpeace.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.liveinpeace.R
import com.example.liveinpeace.viewModel.AuthViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val firstNameField = findViewById<EditText>(R.id.firstNameEditText)
        val lastNameField = findViewById<EditText>(R.id.lastNameEditText)
        val emailField = findViewById<EditText>(R.id.emailEditText)
        val passwordField = findViewById<EditText>(R.id.passwordEditText)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val backButton = findViewById<ImageView>(R.id.backButton)
        val loginPromptTextView = findViewById<TextView>(R.id.loginPromptTextView)
        val genderSpinner = findViewById<Spinner>(R.id.genderSpinner)
        val genderOptions = resources.getStringArray(R.array.gender_options)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            genderOptions
        )

        // Event untuk tombol back
        backButton.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish() // Menutup halaman register agar tidak bisa kembali ke sini dengan tombol back
        }

        registerButton.setOnClickListener {
            val firstName = firstNameField.text.toString()
            val lastName = lastNameField.text.toString()
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            val selectedGender = genderSpinner.selectedItem.toString()
            if (selectedGender == "Pilih Gender") {
                Toast.makeText(this, "Silakan pilih gender!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isNotEmpty() && password.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()) {
                viewModel.register(email, password, firstName, lastName) { success, message ->
                    if (success) {
                        Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                        finish() // Kembali ke login
                    } else {
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Semua data harus diisi!", Toast.LENGTH_SHORT).show()
            }
        }
        loginPromptTextView.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java) // Sesuaikan jika halaman login berbeda
            startActivity(intent)
            finish() // Menutup halaman register agar tidak kembali ke sini saat menekan tombol back
        }

        genderSpinner.adapter = adapter
        genderSpinner.setSelection(0, false)
    }
}
