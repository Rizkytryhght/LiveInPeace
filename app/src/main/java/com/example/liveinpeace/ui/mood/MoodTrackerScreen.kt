package com.example.liveinpeace.ui.mood

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavHostController
import com.example.liveinpeace.model.MoodEntry
import com.example.liveinpeace.ui.features.FeaturesListActivity
import com.example.liveinpeace.viewModel.MoodViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.delay

@Composable
fun MoodTrackerScreen(navController: NavHostController) {
    val viewModel = remember { MoodViewModel() }
    val moods by viewModel.moods
    val hasSubmittedToday by viewModel.hasSubmittedToday
    val isLoading by viewModel.isLoading
    val snackbarHostState = remember { SnackbarHostState() }
    var showSubmittedMessage by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Show Snackbar on initial submission
    LaunchedEffect(hasSubmittedToday) {
        if (hasSubmittedToday) {
            snackbarHostState.showSnackbar("Kamu sudah memilih mood hari ini!")
        }
    }

    // Hide submitted message after 3 seconds
    LaunchedEffect(showSubmittedMessage) {
        if (showSubmittedMessage) {
            delay(3000)
            showSubmittedMessage = false
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    Snackbar(
                        snackbarData = snackbarData,
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(padding)
                .padding(16.dp)
        ) {
            IconButton(
                onClick = {
                    println("Back button clicked")
                    try {
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                            println("Popped back stack")
                        } else {
                            // Fallback: Start FeatureListActivity and finish current
                            context.startActivity(Intent(context, FeaturesListActivity::class.java))
                            (context as? Activity)?.finish()
                            println("Started FeatureListActivity")
                        }
                    } catch (e: Exception) {
                        println("Navigation error: ${e.message}")
                        // Last resort: Finish activity
                        (context as? Activity)?.finish()
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
                    text = "Mood Tracker",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Bagaimana mood kamu hari ini?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF324F5E),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Mood Selection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MoodButton("Marah", "😣", Color(0xFFF44336), hasSubmittedToday, { viewModel.saveMood("Marah") }) { showSubmittedMessage = true }
                    MoodButton("Sedih", "😢", Color(0xFF2196F3), hasSubmittedToday, { viewModel.saveMood("Sedih") }) { showSubmittedMessage = true }
                    MoodButton("Cemas", "😓", Color(0xFFFF9800), hasSubmittedToday, { viewModel.saveMood("Cemas") }) { showSubmittedMessage = true }
                    MoodButton("Tenang", "😊", Color(0xFF4CAF50), hasSubmittedToday, { viewModel.saveMood("Tenang") }) { showSubmittedMessage = true }
                    MoodButton("Senang", "😄", Color(0xFFFFC107), hasSubmittedToday, { viewModel.saveMood("Senang") }) { showSubmittedMessage = true }
                }

                // Submitted Message
                if (showSubmittedMessage) {
                    Text(
                        text = "Kamu sudah memilih mood hari ini!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                    )
                }

                // Loading Indicator or Content
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.padding(top = 32.dp)
                    )
                } else if (moods.isNotEmpty()) {
                    MoodBarChart(moods)
                    Spacer(modifier = Modifier.height(16.dp))
                    WeeklyMoodSummary(moods)
                } else {
                    Text(
                        text = "Belum ada data mood. Pilih mood hari ini!",
                        fontSize = 16.sp,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(top = 32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MoodButton(
    mood: String,
    emoji: String,
    color: Color,
    isDisabled: Boolean,
    onClick: () -> Unit,
    onDisabledClick: () -> Unit
) {
    var clickKey by remember { mutableIntStateOf(0) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isDisabled) Color.Gray else color)
                .clickable(enabled = true) {
                    clickKey++
                    println("Mood button clicked: $mood, key: $clickKey, isDisabled: $isDisabled")
                    if (isDisabled) {
                        onDisabledClick()
                    } else {
                        onClick()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 24.sp
            )
        }
        Text(
            text = mood,
            fontSize = 12.sp,
            color = Color(0xFF333333),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun MoodBarChart(moods: List<MoodEntry>) {
    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                val moodCounts = listOf("Marah", "Sedih", "Cemas", "Tenang", "Senang").mapIndexed { index, mood ->
                    BarEntry(index.toFloat(), moods.count { it.mood == mood }.toFloat())
                }

                // Calculate max frequency for Y-axis
                val maxFrequency = moodCounts.maxOfOrNull { it.y.toInt() }?.toFloat() ?: 1f

                val dataSet = BarDataSet(moodCounts, "Mood").apply {
                    colors = listOf(
                        Color(0xFFF44336).toArgb(),
                        Color(0xFF2196F3).toArgb(),
                        Color(0xFFFF9800).toArgb(),
                        Color(0xFF4CAF50).toArgb(),
                        Color(0xFFFFC107).toArgb()
                    )
                    valueTextColor = Color(0xFF333333).toArgb()
                    valueTextSize = 10f
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return value.toInt().toString()
                        }
                    }
                }

                data = BarData(dataSet).apply {
                    barWidth = 0.3f
                }

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return when (value.toInt()) {
                                0 -> "Marah"
                                1 -> "Sedih"
                                2 -> "Cemas"
                                3 -> "Tenang"
                                4 -> "Senang"
                                else -> ""
                            }
                        }
                    }
                    textColor = Color(0xFF333333).toArgb()
                    textSize = 10f
                    granularity = 1f
                    setDrawGridLines(false)
                    labelRotationAngle = 0f
                }

                axisLeft.apply {
                    textColor = Color(0xFF333333).toArgb()
                    axisMinimum = 0f
                    axisMaximum = maxFrequency + 0.3f // Add padding for readability
                    setDrawGridLines(true)
                }
                axisRight.isEnabled = false

                description.isEnabled = false
                legend.isEnabled = false
                setFitBars(true)
                extraBottomOffset = 10f
                invalidate()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(vertical = 16.dp)
    )
}

@Composable
fun WeeklyMoodSummary(moods: List<MoodEntry>) {
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
                text = "Rekap Mood Mingguan",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2196F3),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            listOf("Marah", "Sedih", "Cemas", "Tenang", "Senang").forEach { mood ->
                val count = moods.count { it.mood == mood }
                Text(
                    text = "$mood: $count hari",
                    fontSize = 16.sp,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}