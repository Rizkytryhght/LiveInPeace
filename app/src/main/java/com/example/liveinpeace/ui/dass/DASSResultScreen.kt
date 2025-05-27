package com.example.liveinpeace.ui.dass

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.liveinpeace.data.local.room.DASSScore
import com.example.liveinpeace.data.repository.DASSRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun DASSResultScreen(navController: NavController, answersString: String) {
    val context = navController.context
    val repository = DASSRepository(
        com.example.liveinpeace.data.local.room.AppDatabase.getDatabase(context).dassScoreDao()
    )
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"

    val decodedAnswers = try {
        URLDecoder.decode(answersString, StandardCharsets.UTF_8.toString())
    } catch (e: Exception) {
        Log.e("DASSResultScreen", "Error decoding answersString: $answersString", e)
        ""
    }

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
        } else emptyMap()
    } catch (e: Exception) {
        Log.e("DASSResultScreen", "Error parsing answers: $decodedAnswers", e)
        emptyMap()
    }

    Log.d("DASSResultScreen", "Parsed answers: $answers")

    if (answers.isEmpty()) {
        ErrorScreen(navController)
        return
    }

    val depressionQuestions = listOf(3, 5, 10, 13, 16, 17, 21)
    val anxietyQuestions = listOf(2, 4, 7, 9, 15, 19, 20)
    val stressQuestions = listOf(1, 6, 8, 11, 12, 14, 18)

    val depressionScore = depressionQuestions.sumOf { answers[it] ?: 0 } * 2
    val anxietyScore = anxietyQuestions.sumOf { answers[it] ?: 0 } * 2
    val stressScore = stressQuestions.sumOf { answers[it] ?: 0 } * 2

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

    LaunchedEffect(answers) {
        if (answers.isNotEmpty()) {
            val score = DASSScore(
                userId = userId,
                depressionScore = depressionScore,
                anxietyScore = anxietyScore,
                stressScore = stressScore,
                timestamp = System.currentTimeMillis()
            )
            withContext(Dispatchers.IO) {
                repository.insertScore(score)
            }
            Log.d("DASSResultScreen", "Saved score: $score")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hasil Kuesioner DASS-21",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2196F3),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        ResultCard("Depresi", depressionScore, depressionLevel)
        ResultCard("Kecemasan", anxietyScore, anxietyLevel)
        ResultCard("Stres", stressScore, stressLevel)

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Text(
                text = "Catatan: Skor dan interpretasi ini adalah hasil awal. Konsultasikan dengan profesional kesehatan mental untuk penilaian lebih lanjut.",
                fontSize = 14.sp,
                color = Color(0xFF333333),
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                Log.d("DASSResultScreen", "Kembali button clicked")
                navController.navigate("dass_introduction") {
                    popUpTo("dass_result/{answers}") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Kembali", color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
fun ErrorScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Error", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF44336))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Gagal memuat hasil kuesioner. Silakan coba lagi.",
                    fontSize = 16.sp,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        navController.navigate("dass_introduction") {
                            popUpTo("dass_introduction") { inclusive = false }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Kembali ke Pengenalan", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}
