package com.example.liveinpeace.ui.auth

import android.annotation.SuppressLint
import androidx.compose.material3.Button
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.liveinpeace.R
import com.example.liveinpeace.data.ProfileModel
import com.example.liveinpeace.data.repository.ProfileRepository
import com.example.liveinpeace.viewModel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liveinpeace.ui.theme.GreenPrimary
import kotlinx.coroutines.withContext

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel(factory = ViewModelProvider.NewInstanceFactory())
) {
    // State untuk form
    val firstName = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val selectedGender = remember { mutableStateOf("Pilih Gender") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val genderOptions = listOf("Pilih Gender", "Laki-laki", "Perempuan")

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .background(color = Color.White),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Back Button
        IconButton(onClick = { onBackClick() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Text(
            text = "Register",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Create an account to continue!",
            fontSize = 14.sp,
            color = Color.Gray
        )

        // First name, last name, email, password
        OutlinedTextField(
            value = firstName.value,
            onValueChange = { firstName.value = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = lastName.value,
            onValueChange = { lastName.value = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle Password"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        // Dropdown gender
        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedTextField(
                value = selectedGender.value,
                onValueChange = {},
                label = { Text("Gender") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                genderOptions.forEach { gender ->
                    DropdownMenuItem(
                        text = { Text(text = gender) },
                        onClick = {
                            selectedGender.value = gender
                            expanded = false
                        }
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = true }
            )
        }

        // Phone number field
        OutlinedTextField(
            value = phone.value,
            onValueChange = { phone.value = it },
            label = { Text("Phone Number") },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.flag_indo),
                    contentDescription = "Indonesian Flag",
                    modifier = Modifier.size(24.dp)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Register button
        Button(
            onClick = {
                if (selectedGender.value == "Pilih Gender") {
                    Toast.makeText(context, "Silakan pilih gender!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (email.value.isNotEmpty() && password.value.isNotEmpty() &&
                    firstName.value.isNotEmpty() && lastName.value.isNotEmpty()
                ) {
                    viewModel.register(
                        email.value,
                        password.value,
                        firstName.value,
                        lastName.value,
                        selectedGender.value,
                        phone.value
                    ) { success, message ->
                        if (success) {
                            coroutineScope.launch {
                                withContext(Dispatchers.IO) {
                                    val profile = ProfileModel(
                                        firstName = firstName.value,
                                        lastName = lastName.value,
                                        email = email.value,
                                        gender = selectedGender.value,
                                        phoneNumber = phone.value,
                                        profileImagePath = ""
                                    )
                                    ProfileRepository(context).saveProfile(profile)
                                }
                            }
                            Toast.makeText(context, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                            onRegisterSuccess()
                        } else {
                            Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Semua data harus diisi!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(30.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            TextButton(onClick = onBackClick) {
                Text(
                    text = "Already have an account? Log in",
                    color = GreenPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}