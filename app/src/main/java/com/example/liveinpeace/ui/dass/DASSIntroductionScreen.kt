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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign

@Composable
fun DASSIntroductionScreen(navController: NavController) {
    var currentPage by remember { mutableIntStateOf(1) }
    val totalPages = 3
    val greenColor = Color(0xFF4CAF50)
    val backgroundColor = Color(0xFFF5F7FA)

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Indikator Halaman
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            for (i in 1..totalPages) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .padding(4.dp)
                        .background(
                            color = if (i == currentPage) greenColor else Color.Gray.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(5.dp)
                        )
                )
            }
        }

        // Konten dalam Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .weight(1f, fill = false),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (currentPage) {
                    1 -> {
                        Text(
                            text = "Untuk Apa Kuesioner Ini?",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Pengisian kuesioner ini sebagai bagian dari proses skrining awal kondisi psikologis, khususnya yang berkaitan dengan depresi, kecemasan, dan stres yang mungkin sedang kamu alami. Instrumen yang digunakan adalah DASS-21, versi singkat dari DASS normal, yang telah terbukti valid dan reliabel dalam mengukur gejala emosional tersebut.",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "DASS-21 adalah alat ukur psikologis yang umum digunakan untuk orang dewasa, termasuk mahasiswa, dan terdiri dari 21 pertanyaan berdasarkan pengalaman Anda selama 7 hari terakhir.",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                    2 -> {
                        Text(
                            text = "Privasi Data",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Kerahasiaan data Anda akan dijamin sepenuhnya. Informasi yang diberikan tidak akan disebarluaskan dan hanya akan digunakan untuk keperluan asesmen psikologis awal.",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                    3 -> {
                        Text(
                            text = "Instruksi Pengisian",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Anda akan diminta mengisi setiap soal dengan mengklik pilihan yang paling sesuai dengan pengalaman Anda selama 1 minggu terakhir. Terdapat empat pilihan jawaban:\n" +
                                    "0 - Tidak sesuai dengan saya sama sekali, atau tidak pernah\n" +
                                    "1 - Sesuai dengan saya sampai tingkat tertentu, atau kadang-kadang\n" +
                                    "2 - Sesuai dengan saya sampai batas yang dapat dipertimbangkan, atau sering\n" +
                                    "3 - Sangat sesuai dengan saya, atau sangat sering",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tidak ada jawaban benar atau salah, jadi mohon isi dengan jujur sesuai kondisi yang Anda alami.",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Tombol Navigasi
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Tombol Back selalu ada, di halaman 1 akan kembali ke dass_options
            Button(
                onClick = {
                    if (currentPage == 1) {
                        navController.popBackStack("dass_options", false)
                    } else {
                        currentPage--
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(text = "Back", color = Color.White, fontSize = 16.sp)
            }

            // Tombol Next atau Mulai Tes
            Button(
                onClick = {
                    if (currentPage < totalPages) {
                        currentPage++
                    } else {
                        navController.navigate("dass_questionnaire")
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = if (currentPage < totalPages) "Next" else "Mulai Tes",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}