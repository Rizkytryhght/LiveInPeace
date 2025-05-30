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

@OptIn(ExperimentalMaterial3Api::class)
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

    // State untuk filter bulan
    val calendar = Calendar.getInstance()
    var selectedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var expanded by remember { mutableStateOf(false) }

    // List opsi bulan (12 bulan terakhir)
    val monthOptions = (0..11).map { month ->
        calendar.apply {
            set(Calendar.MONTH, month)
        }
        "${getMonthName(month)} ${calendar.get(Calendar.YEAR)}" to Pair(calendar.get(Calendar.YEAR), month)
    }.reversed()

    LaunchedEffect(selectedYear, selectedMonth) {
        coroutineScope.launch {
            try {
                println("Loading scores for userId: $userId, $selectedYear-$selectedMonth")
                scores = repository.getScoresByMonth(userId, selectedYear, selectedMonth)
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
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Dropdown filter bulan
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                TextField(
                    value = "${getMonthName(selectedMonth)} $selectedYear",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Bulan") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color(0xFF4CAF50),
                        unfocusedIndicatorColor = Color(0xFF4CAF50)
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    monthOptions.forEach { (label, yearMonth) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                selectedYear = yearMonth.first
                                selectedMonth = yearMonth.second
                                expanded = false
                            }
                        )
                    }
                }
            }

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
                        text = "Belum ada riwayat kuesioner untuk ${getMonthName(selectedMonth)} $selectedYear",
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

                val barData = BarData(dataSetDepression, dataSetAnxiety, dataSetStress).apply {
                    barWidth = 0.3f // Lebar tiap batang
                }

                // Atur jarak antar grup batang, grup pertama lebih dekat ke 0
                barData.groupBars(-0.3f, 0.1f, 0.02f) // start, groupSpace, barSpace

                data = barData

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val index = value.toInt()
                            return if (index in scores.indices) {
                                formatTimestampShort(scores.reversed()[index].timestamp)
                            } else ""
                        }
                    }
                    textColor = Color(0xFF333333).toArgb()
                    textSize = 10f // Kecilkan ukuran teks
//                    labelRotationAngle = 45f // Putar label 45 derajat
                    granularity = 1f
                    setDrawGridLines(false)
                    axisMinimum = -0.5f // Sesuaikan dengan start groupBars
                    setCenterAxisLabels(true) // Pusatkan label di bawah grup batang
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
                extraBottomOffset = 10f // Tambah padding bawah biar label nggak kepotong
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

private fun formatTimestampShort(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("dd MMM", Locale("id", "ID"))
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "Tanggal tidak valid"
    }
}

private fun getMonthName(month: Int): String {
    return when (month) {
        0 -> "Januari"
        1 -> "Februari"
        2 -> "Maret"
        3 -> "April"
        4 -> "Mei"
        5 -> "Juni"
        6 -> "Juli"
        7 -> "Agustus"
        8 -> "September"
        9 -> "Oktober"
        10 -> "November"
        11 -> "Desember"
        else -> ""
    }
}