package com.example.liveinpeace.ui.dass

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
    val totalPages = 2
    val greenColor = Color(0xFFA5D6A7)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        when (currentPage) {
            1 -> {
                Text(
                    text = "Apa itu DASS-21?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Merasa cemas, stres, atau sedih berkepanjangan?\n" +
                            "Tes ini membantu kamu mengenali tingkat depresi, kecemasan, dan stres yang mungkin sedang kamu alami.",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "DASS-21 adalah alat ukur psikologis yang umum digunakan untuk orang dewasa, termasuk mahasiswa, dan terdiri dari 21 pertanyaan singkat.\n" +
                            "Hasilnya bukan diagnosis, tapi bisa jadi langkah awal untuk lebih memahami kondisi mental kamu.",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Jangan khawatir — semua jawaban bersifat pribadi dan tidak dibagikan ke siapa pun",
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
                    text = "Petunjuk dan Privasi",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text =  "Petunjuk: Baca setiap pernyataan dan pilih angka yang sesuai dengan pengalaman Anda:\n" +
                            "0 - Tidak pernah\n" +
                            "1 - Kadang-kadang\n" +
                            "2 - Sering\n" +
                            "3 - Sangat sering",
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
                        Text(text = "Mulai Tes")
                    }
                }
            }
        }
    }
}