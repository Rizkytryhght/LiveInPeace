package com.example.liveinpeace.ui.dass

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.mutableStateMapOf

@Composable
fun DASSQuestionnaireScreen(navController: NavController) {
    var currentQuestion by remember { mutableStateOf(1) }
    val answers = remember { mutableStateMapOf<Int, Int>() }
    val totalQuestions = 21
    val greenColor = Color(0xFF4CAF50)

    // Daftar pertanyaan
    val questions = listOf(
        "I found it hard to wind down",
        "I was aware of dryness of my mouth",
        "I couldn't seem to experience any positive feeling at all",
        "I experienced breathing difficulty (eg, excessively rapid breathing, breathlessness in the absence of physical exertion)",
        "I found it difficult to work up the initiative to do things",
        "I tended to over-react to situations",
        "I experienced trembling (eg, in the hands)",
        "I felt that I was using a lot of nervous energy",
        "I was worried about situations in which I might panic and make a fool of myself",
        "I felt that I had nothing to look forward to",
        "I found myself getting agitated",
        "I found it difficult to relax",
        "I felt down-hearted and blue",
        "I was intolerant of anything that kept me from getting on with what I was doing",
        "I felt I was close to panic",
        "I was unable to become enthusiastic about anything",
        "I felt I wasn't worth much as a person",
        "I felt that I was rather touchy",
        "I was aware of the action of my heart in the absence of physical exertion (eg, sense of heart rate increase, heart missing a beat)",
        "I felt scared without any good reason",
        "I felt that life was meaningless"
    )

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

        // Opsi jawaban
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
                        navController.navigate("dass_result")
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = greenColor)
            ) {
                Text(text = if (currentQuestion < totalQuestions) "Next" else "Selesai")
            }
        }
    }
}