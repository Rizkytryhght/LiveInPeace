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

//class OtpVerificationActivity : AppCompatActivity() {
//    private lateinit var viewModel: AuthViewModel
//    private lateinit var otpField: EditText
//    private lateinit var verifyButton: Button
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_otp_verification)
//
//        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
//        otpField = findViewById(R.id.otpEditText)
//        verifyButton = findViewById(R.id.verifyButton)
//
//        val email = intent.getStringExtra("email")
//
//        verifyButton.setOnClickListener {
//            val otp = otpField.text.toString()
//
//            if (otp.isNotEmpty()) {
//                viewModel.verifyOtp(email!!, otp) { success ->
//                    if (success) {
//                        val intent = Intent(this, ResetPasswordActivity::class.java)
//                        intent.putExtra("email", email)
//                        startActivity(intent)
//                    } else {
//                        Toast.makeText(this, "OTP salah!", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            } else {
//                Toast.makeText(this, "Masukkan OTP!", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}
