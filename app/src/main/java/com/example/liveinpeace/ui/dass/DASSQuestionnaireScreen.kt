package com.example.liveinpeace.ui.dass

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun DASSQuestionnaireScreen(navController: NavController) {
    var currentQuestion by remember { mutableStateOf(1) }
    val answers = remember { mutableStateMapOf<Int, Int>() }
    val totalQuestions = 21
    var showDialog by remember { mutableStateOf(false) }
    var showBackDialog by remember { mutableStateOf(false) }

    val questions = listOf(
        "Saya merasa sulit untuk menenangkan diri",
        "Saya menyadari mulut saya terasa kering",
        "Saya tidak bisa merasakan perasaan positif sama sekali",
        "Saya mengalami kesulitan bernapas (misalnya, bernapas terlalu cepat, terengah-engah saat tidak melakukan aktivitas fisik)",
        "Saya merasa sulit untuk mengambil inisiatif untuk melakukan sesuatu",
        "Saya cenderung bereaksi berlebihan terhadap situasi",
        "Saya mengalami gemetar (misalnya, di tangan)",
        "Saya merasa bahwa saya menggunakan banyak energi karena gugup",
        "Saya khawatir dengan situasi di mana saya mungkin panik dan mempermalukan diri saya sendiri",
        "Saya merasa tidak ada yang bisa saya nantikan",
        "Saya merasa gelisah",
        "Saya merasa sulit untuk rileks",
        "Saya merasa sedih dan murung",
        "Saya tidak toleran terhadap apa pun yang menghalangi saya untuk melanjutkan apa yang sedang saya kerjakan",
        "Saya merasa hampir panik",
        "Saya tidak bisa antusias terhadap apa pun",
        "Saya merasa saya tidak terlalu berharga sebagai manusia",
        "Saya merasa bahwa saya agak sensitif",
        "Saya sadar akan tindakan jantung saya tanpa adanya aktivitas fisik (misalnya, rasa detak jantung meningkat, jantung tidak berdetak)",
        "Saya merasa takut tanpa alasan yang kuat",
        "Saya merasa hidup tidak ada artinya"
    )

    val allQuestionsAnswered = answers.size == totalQuestions

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Pertanyaan ${currentQuestion}/$totalQuestions",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2196F3),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Text(
                text = questions[currentQuestion - 1],
                fontSize = 18.sp,
                color = Color(0xFF333333),
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Opsi jawaban
        val options = listOf(
            "0 - Tidak pernah",
            "1 - Kadang-kadang",
            "2 - Sering",
            "3 - Sangat sering"
        )
        Column {
            options.forEachIndexed { index, option ->
                QuestionItem(
                    option = option,
                    isSelected = answers[currentQuestion] == index,
                    onClick = { answers[currentQuestion] = index }
                )
            }
        }

        // Tombol navigasi
        NavigationButtons(
            onBackClick = { currentQuestion-- },
            onNextClick = {
                if (currentQuestion < totalQuestions) {
                    currentQuestion++
                } else {
                    if (allQuestionsAnswered) {
                        showDialog = true
                    }
                }
            },
            isFirstQuestion = currentQuestion == 1,
            isLastQuestion = currentQuestion == totalQuestions,
            isAnswered = answers.containsKey(currentQuestion),
            showBackDialog = { showBackDialog = it }
        )
    }

    // AlertDialog untuk konfirmasi submit
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Konfirmasi Pengiriman", fontSize = 20.sp, color = Color(0xFF2196F3)) },
            text = { Text("Apakah Anda yakin ingin mengirim jawaban kuesioner sekarang?", fontSize = 16.sp) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        val answersString = answers.entries.joinToString(",") { "${it.key}:${it.value}" }
                        val encodedAnswers = URLEncoder.encode(answersString, StandardCharsets.UTF_8.toString())
                        Log.d("DASSQuestionnaire", "Navigating with answers: $answersString")
                        navController.navigate("dass_result/$encodedAnswers")
                    }
                ) {
                    Text("Ya", color = Color(0xFF4CAF50), fontSize = 16.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Tidak", color = Color.Red, fontSize = 16.sp)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // AlertDialog untuk konfirmasi kembali
    if (showBackDialog) {
        AlertDialog(
            onDismissRequest = { showBackDialog = false },
            title = { Text("Konfirmasi Kembali", fontSize = 20.sp, color = Color(0xFF2196F3)) },
            text = { Text("Apakah Anda yakin ingin kembali ke halaman pengenalan? Progres tes akan hilang.", fontSize = 16.sp) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showBackDialog = false
                        navController.navigate("dass_introduction") {
                            popUpTo("dass_questionnaire") { inclusive = true }
                        }
                    }
                ) {
                    Text("Ya", color = Color(0xFF4CAF50), fontSize = 16.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showBackDialog = false }
                ) {
                    Text("Tidak", color = Color.Red, fontSize = 16.sp)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}