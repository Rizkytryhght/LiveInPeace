package com.example.liveinpeace.ui.dass

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun DASSResultScreen(navController: NavController) {
    val greenColor = Color(0xFF4CAF50)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Hasil Kuesioner DASS-21",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Halaman ini akan menampilkan hasil kuesioner (placeholder).",
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
        Button(
            onClick = { navController.navigate("dass_introduction") },
            modifier = Modifier.padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = greenColor)
        ) {
            Text(text = "Kembali ke Pengenalan")
        }
    }
}