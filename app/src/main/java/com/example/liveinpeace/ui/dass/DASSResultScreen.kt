package com.example.liveinpeace.ui.dass

import androidx.compose.foundation.layout.*
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
import android.util.Log
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun DASSResultScreen(navController: NavController, answersString: String) {
    val greenColor = Color(0xFF4CAF50)

    // Decode answersString
    val decodedAnswers = try {
        URLDecoder.decode(answersString, StandardCharsets.UTF_8.toString())
    } catch (e: Exception) {
        Log.e("DASSResultScreen", "Error decoding answersString: $answersString", e)
        ""
    }

    // Parse answersString ke Map
    val answers = try {
        if (decodedAnswers.isNotEmpty()) {
            decodedAnswers.split(",")
                .filter { it.isNotEmpty() }
                .associate {
                    val parts = it.split(":")
                    if (parts.size == 2) {
                        parts[0].toInt() to parts[1].toInt()
                    } else {
                        throw IllegalArgumentException("Invalid answer format: $it")
                    }
                }
        } else {
            emptyMap()
        }
    } catch (e: Exception) {
        Log.e("DASSResultScreen", "Error parsing answers: $decodedAnswers", e)
        emptyMap()
    }

    Log.d("DASSResultScreen", "Parsed answers: $answers")

    // Jika answers kosong, tampilkan pesan error
    if (answers.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Error",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Gagal memuat hasil kuesioner. Silakan coba lagi.",
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
            Button(
                onClick = { navController.navigate("dass_introduction") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = greenColor)
            ) {
                Text(text = "Kembali ke Pengenalan")
            }
        }
        return
    }

    // Definisikan pertanyaan untuk setiap skala
    val depressionQuestions = listOf(3, 5, 10, 13, 16, 17, 21)
    val anxietyQuestions = listOf(2, 4, 7, 9, 15, 19, 20)
    val stressQuestions = listOf(1, 6, 8, 11, 12, 14, 18)

    // Hitung skor
    val depressionScore = depressionQuestions.sumOf { answers[it] ?: 0 } * 2
    val anxietyScore = anxietyQuestions.sumOf { answers[it] ?: 0 } * 2
    val stressScore = stressQuestions.sumOf { answers[it] ?: 0 } * 2

    // Interpretasi skor
    val depressionLevel = when (depressionScore) {
        in 0..9 -> "Normal"
        in 10..13 -> "Ringan"
        in 14..20 -> "Sedang"
        in 21..27 -> "Berat"
        else -> "Sangat Berat"
    }
    val anxietyLevel = when (anxietyScore) {
        in 0..7 -> "Normal"
        in 8..9 -> "Ringan"
        in 10..14 -> "Sedang"
        in 15..19 -> "Berat"
        else -> "Sangat Berat"
    }
    val stressLevel = when (stressScore) {
        in 0..14 -> "Normal"
        in 15..18 -> "Ringan"
        in 19..25 -> "Sedang"
        in 26..33 -> "Berat"
        else -> "Sangat Berat"
    }

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
        Spacer(modifier = Modifier.height(16.dp))

        // Tampilkan skor dan interpretasi
        Text(
            text = "Depresi",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Skor: $depressionScore ($depressionLevel)",
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Kecemasan",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Skor: $anxietyScore ($anxietyLevel)",
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Stres",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Skor: $stressScore ($stressLevel)",
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Catatan tambahan
        Text(
            text = "Catatan: Skor dan interpretasi ini adalah hasil awal. Konsultasikan dengan profesional kesehatan mental untuk penilaian lebih lanjut.",
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = { navController.navigate("dass_introduction") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = greenColor)
        ) {
            Text(text = "Kembali ke Pengenalan")
        }
    }
}