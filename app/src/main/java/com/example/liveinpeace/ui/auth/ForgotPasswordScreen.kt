package com.example.liveinpeace.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liveinpeace.ui.theme.GreenPrimary
import com.example.liveinpeace.viewModel.AuthViewModel

@Composable
fun ForgotPasswordScreen(
    onBackClick: () -> Unit,
    viewModel: AuthViewModel = viewModel(),
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // Back button
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "Forgot Password?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Please enter the email address linked with your account",
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = Color(0xFF666666),
                    focusedLabelColor = GreenPrimary,
                    unfocusedLabelColor = Color(0xFF666666),
                    cursorColor = GreenPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Reset Password Button
            Button(
                onClick = {
                    if (email.isNotEmpty()) {
                        viewModel.resetPassword(email) { success, message ->
                            if (success) {
                                Toast.makeText(
                                    context,
                                    "Cek email untuk reset password",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onBackClick() // Kembali ke login
                            } else {
                                Toast.makeText(
                                    context,
                                    "Error: $message",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Email harus diisi!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text(text = "Reset Password", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Login Prompt
        Text(
            text = "Remember Password? Log in",
            color = GreenPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { onBackClick() }
                .padding(bottom = 36.dp)
        )
    }
}