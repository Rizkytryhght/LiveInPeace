package com.example.liveinpeace.ui.dass

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.liveinpeace.data.local.room.DASSScore
import com.example.liveinpeace.data.repository.DASSRepository
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DASSHistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = DASSRepository(
        com.example.liveinpeace.data.local.room.AppDatabase.getDatabase(context).dassScoreDao(),
        FirebaseFirestore.getInstance()
    )
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"
    val coroutineScope = rememberCoroutineScope()
    var scores by remember { mutableStateOf<List<DASSScore>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                println("Loading scores for userId: $userId")
                scores = repository.getAllScores(userId)
                println("Scores loaded: ${scores.size}")
            } catch (e: Exception) {
                errorMessage = "Gagal memuat riwayat: ${e.message}"
                println("Error loading scores: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(16.dp)
    ) {
        // Tombol Back
        IconButton(
            onClick = {
                navController.navigate("dass_options") {
                    popUpTo("dass_history") { inclusive = true }
                }
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.9f))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Riwayat Kuesioner DASS",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2196F3),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when {
                isLoading -> {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Terjadi kesalahan",
                        fontSize = 18.sp,
                        color = Color(0xFFF44336),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 32.dp)
                    )
                }
                scores.isEmpty() -> {
                    Text(
                        text = "Belum ada riwayat kuesioner",
                        fontSize = 18.sp,
                        color = Color(0xFF333333),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 32.dp)
                    )
                }
                else -> {
                    // Grafik
                    BarChartView(scores = scores)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Daftar Skor
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(scores) { score ->
                            ScoreCard(score)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BarChartView(scores: List<DASSScore>) {
    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                val entriesDepression = scores.reversed().mapIndexed { index, score ->
                    BarEntry(index.toFloat(), score.depressionScore.toFloat())
                }
                val entriesAnxiety = scores.reversed().mapIndexed { index, score ->
                    BarEntry(index.toFloat() + 0.33f, score.anxietyScore.toFloat())
                }
                val entriesStress = scores.reversed().mapIndexed { index, score ->
                    BarEntry(index.toFloat() + 0.66f, score.stressScore.toFloat())
                }

                val dataSetDepression = BarDataSet(entriesDepression, "Depresi").apply {
                    color = Color(0xFF2196F3).toArgb()
                    valueTextColor = Color(0xFF333333).toArgb()
                    valueTextSize = 10f
                }
                val dataSetAnxiety = BarDataSet(entriesAnxiety, "Kecemasan").apply {
                    color = Color(0xFF4CAF50).toArgb()
                    valueTextColor = Color(0xFF333333).toArgb()
                    valueTextSize = 10f
                }
                val dataSetStress = BarDataSet(entriesStress, "Stres").apply {
                    color = Color(0xFFF44336).toArgb()
                    valueTextColor = Color(0xFF333333).toArgb()
                    valueTextSize = 10f
                }

                data = BarData(dataSetDepression, dataSetAnxiety, dataSetStress).apply {
                    barWidth = 0.3f // Lebar tiap batang
                }

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val index = value.toInt()
                            return if (index in scores.indices) {
                                formatTimestamp(scores.reversed()[index].timestamp)
                            } else ""
                        }
                    }
                    textColor = Color(0xFF333333).toArgb()
                    granularity = 1f
                    setDrawGridLines(false)
                }

                axisLeft.apply {
                    textColor = Color(0xFF333333).toArgb()
                    axisMinimum = 0f
                    setDrawGridLines(true)
                }
                axisRight.isEnabled = false

                description.isEnabled = false
                legend.textColor = Color(0xFF333333).toArgb()
                setFitBars(true)
                invalidate() // Refresh chart
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
    )
}

@Composable
fun ScoreCard(score: DASSScore) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = formatTimestamp(score.timestamp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4CAF50),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ScoreItem("Depresi", score.depressionScore)
                ScoreItem("Kecemasan", score.anxietyScore)
                ScoreItem("Stres", score.stressScore)
            }
        }
    }
}

@Composable
fun ScoreItem(label: String, score: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF333333)
        )
        Text(
            text = score.toString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2196F3)
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "Tanggal tidak valid"
    }
}