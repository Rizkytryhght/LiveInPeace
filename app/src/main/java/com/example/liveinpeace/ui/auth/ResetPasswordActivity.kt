package com.example.liveinpeace.ui.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.liveinpeace.R
import com.example.liveinpeace.viewmodel.AuthViewModel

//class ResetPasswordActivity : AppCompatActivity() {
//    private lateinit var viewModel: AuthViewModel
//    private lateinit var newPasswordField: EditText
//    private lateinit var confirmPasswordField: EditText
//    private lateinit var resetButton: Button
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_reset_password)
//
//        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
//        newPasswordField = findViewById(R.id.newPasswordEditText)
//        confirmPasswordField = findViewById(R.id.confirmPasswordEditText)
//        resetButton = findViewById(R.id.resetPasswordButton)
//
//        val email = intent.getStringExtra("email")
//
//        resetButton.setOnClickListener {
//            val newPassword = newPasswordField.text.toString()
//            val confirmPassword = confirmPasswordField.text.toString()
//
//            if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty()) {
//                if (newPassword == confirmPassword) {
//                    viewModel.updatePassword(email!!, newPassword) { success ->
//                        if (success) {
//                            val intent = Intent(this, PasswordChangedActivity::class.java)
//                            startActivity(intent)
//                        } else {
//                            Toast.makeText(this, "Gagal reset password!", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                } else {
//                    Toast.makeText(this, "Password tidak cocok!", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                Toast.makeText(this, "Isi semua field!", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}
