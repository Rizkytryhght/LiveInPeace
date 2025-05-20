package com.example.liveinpeace.ui.dass

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

@Composable
fun DASSQuestionnaireScreen(navController: NavController) {
    var currentQuestion by remember { mutableStateOf(1) }
    val answers = remember { mutableStateMapOf<Int, Int>() }
    val totalQuestions = 21
    val greenColor = Color(0xFF4CAF50)
    var showDialog by remember { mutableStateOf(false) }

    // Daftar pertanyaan dari PDF (hardcoded)
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

    // Validasi: Cek apakah semua pertanyaan sudah dijawab
    val allQuestionsAnswered = answers.size == totalQuestions

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Judul pertanyaan
        Text(
            text = "Pertanyaan ${currentQuestion}/$totalQuestions",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Teks pertanyaan
        Text(
            text = questions[currentQuestion - 1],
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Opsi jawaban (Radio Buttons)
        val options = listOf(
            "0 - Tidak pernah",
            "1 - Kadang-kadang",
            "2 - Sering",
            "3 - Sangat sering"
        )
        options.forEachIndexed { index, option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = answers[currentQuestion] == index,
                    onClick = { answers[currentQuestion] = index }
                )
                Text(
                    text = option,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Tombol navigasi
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentQuestion > 1) {
                Button(
                    onClick = { currentQuestion-- },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = greenColor)
                ) {
                    Text(text = "Back")
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            Button(
                onClick = {
                    if (currentQuestion < totalQuestions) {
                        currentQuestion++
                    } else {
                        if (allQuestionsAnswered) {
                            showDialog = true // Tampilkan dialog konfirmasi
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                enabled = if (currentQuestion < totalQuestions) {
                    answers.containsKey(currentQuestion) // Pastikan jawaban dipilih untuk "Next"
                } else {
                    allQuestionsAnswered // Pastikan semua pertanyaan dijawab untuk "Selesai"
                }
            ) {
                Text(text = if (currentQuestion < totalQuestions) "Next" else "Selesai")
            }
        }
    }

    // AlertDialog untuk konfirmasi submit
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Konfirmasi Pengiriman") },
            text = { Text("Apakah Anda yakin ingin mengirim jawaban kuesioner sekarang?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        navController.navigate("dass_result")
                    }
                ) {
                    Text("Ya")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Tidak")
                }
            }
        )
    }
}