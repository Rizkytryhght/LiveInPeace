package com.example.liveinpeace.ui.dass

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Color

@Composable
fun DASSIntroductionScreen(navController: NavController) {
    var currentPage by remember { mutableStateOf(1) }
    val totalPages = 3
    val greenColor = Color(0xFF4CAF50)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        when (currentPage) {
            1 -> {
                Text(
                    text = "Untuk apa kuesioner ini?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Pengisian kuesioner ini sebagai bagian dari proses skrining awal kondisi psikologis\n" +
                            "Khususnya yang berkaitan dengan depresi, kecemasan, dan stres yang mungkin sedang kamu alami.\n" +
                            "Instrumen yang digunakan adalah DASS-21 yang merupakan versi singkat dari versi normalnya.\n" +
                            "Depression Anxiety Stress Scales (DASS) telah terbukti valid dan reliabel dalam mengukur gejala emosional tersebut.",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "DASS-21 adalah alat ukur psikologis yang umum digunakan untuk orang dewasa, termasuk mahasiswa, dan terdiri dari 21 pertanyaan, " +
                            "yang mengacu pada pengalaman anda selama 7 hari terakhir.",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (currentPage > 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { currentPage-- },
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = greenColor)
                        ) {
                            Text(text = "Back")
                        }
                        Button(
                            onClick = {
                                if (currentPage < totalPages) {
                                    currentPage++
                                } else {
                                    navController.navigate("dass_questionnaire")
                                }
                            },
                            modifier = Modifier.weight(1f).padding(start = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = greenColor)
                        ) {
                            Text(text = if (currentPage < totalPages) "Next" else "Mulai Tes")
                        }
                    }
                } else {
                    Button(
                        onClick = { currentPage++ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = greenColor)
                    ) {
                        Text(text = "Next")
                    }
                }
            }
            2 -> {
                Text(
                    text = "Privasi data",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text =  "Kerahasiaan data Anda akan dijamin sepenuhnya.\n" +
                            "Informasi yang diberikan tidak akan disebarluaskan dan hanya akan digunakan untuk keperluan asesmen psikologis awal.",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (currentPage > 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { currentPage-- },
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = greenColor)
                        ) {
                            Text(text = "Back")
                        }
                        Button(
                            onClick = {
                                if (currentPage < totalPages) {
                                    currentPage++
                                } else {
                                    navController.navigate("dass_questionnaire") // Ke halaman tes
                                }
                            },
                            modifier = Modifier.weight(1f).padding(start = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = greenColor)
                        ) {
                            Text(text = if (currentPage < totalPages) "Next" else "Mulai Tes")
                        }
                    }
                } else {
                    Button(
                        onClick = { currentPage++ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = greenColor)
                    ) {
                        Text(text = "Next")
                    }
                }
            }
            3 -> {
                Text(
                    text = "Instruksi pengisian",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text =  "Anda akan diminta untuk mengisi setiap soal yang ada, dengan mengklik pilihan yang paling sesuai dengan pengalaman anda selama 1 minggu terakhir.  \n" +
                            "Terdapat empat pilihan jawaban yang disediakan untuk setiap pertanyaan yaitu:\n" +
                            "0 - Tidak sesuai dengan saya sama sekali, atau tidak pernah\n" +
                            "1 - Sesuai dengan saya sampai tingkat tertentu, atau Kadang-kadang\n" +
                            "2 - Sesuai dengan saya sampai batas yang dapat dipertimbangkan, atau sering\n" +
                            "3 - Sangat sesuai dengan saya, atau sangat sering",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text =  "Tidak ada jawaban yang benar ataupun salah, karena itu mohon isi dengan jujur sesuai dengan kondisi yang anda alami.\n",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (currentPage > 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { currentPage-- },
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = greenColor)
                        ) {
                            Text(text = "Back")
                        }
                        Button(
                            onClick = {
                                if (currentPage < totalPages) {
                                    currentPage++
                                } else {
                                    navController.navigate("dass_questionnaire")
                                }
                            },
                            modifier = Modifier.weight(1f).padding(start = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = greenColor)
                        ) {
                            Text(text = if (currentPage < totalPages) "Next" else "Mulai Tes")
                        }
                    }
                } else {
                    Button(
                        onClick = { currentPage++ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = greenColor)
                    ) {
                        Text(text = "Mulai Tes")
                    }
                }
            }
        }
    }
}