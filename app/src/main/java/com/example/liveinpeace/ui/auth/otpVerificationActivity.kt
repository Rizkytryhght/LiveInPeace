package com.example.liveinpeace.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.liveinpeace.R
import com.example.liveinpeace.viewModel.AuthViewModel

class OtpVerificationActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel
    private lateinit var otpField: EditText
    private lateinit var verifyButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        otpField = findViewById(R.id.otpPinView)
        verifyButton = findViewById(R.id.verifyButton)

        val email = intent.getStringExtra("email")
        val phoneNumber = intent.getStringExtra("phoneNumber") ?: ""

        if (phoneNumber.isNotEmpty()) {
            viewModel.sendOtp(phoneNumber, this) { success, message ->
                if (success) {
                    Toast.makeText(this, "OTP telah dikirim.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, message ?: "Gagal mengirim OTP", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Nomor telepon tidak ditemukan.", Toast.LENGTH_SHORT).show()
        }

        verifyButton.setOnClickListener {
            val otp = otpField.text.toString()

            if (otp.isNotEmpty()) {
                viewModel.verifyOtp(otp) { success ->
                    if (success) {
                        val intent = Intent(this, ResetPasswordActivity::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "OTP salah!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Masukkan OTP!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}