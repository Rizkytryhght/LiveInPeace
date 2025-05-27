package com.example.liveinpeace.ui.reminder

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liveinpeace.viewModel.ReminderViewModel
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ReminderScreen() {
    val viewModel: ReminderViewModel = viewModel()
    val context = LocalContext.current

    // Collect prayer times
    val fajrTime by viewModel.fajrTime.collectAsState()
    val dhuhrTime by viewModel.dhuhrTime.collectAsState()
    val asrTime by viewModel.asrTime.collectAsState()
    val maghribTime by viewModel.maghribTime.collectAsState()
    val ishaTime by viewModel.ishaTime.collectAsState()

    // For legacy support
    val dzikirPagiTime by viewModel.dzikirPagiTime.collectAsState()
    val dzikirPetangTime by viewModel.dzikirPetangTime.collectAsState()

    // Track reminder states
    var fajrEnabled by remember { mutableStateOf(true) }
    var dhuhrEnabled by remember { mutableStateOf(true) }
    var asrEnabled by remember { mutableStateOf(true) }
    var maghribEnabled by remember { mutableStateOf(true) }
    var ishaEnabled by remember { mutableStateOf(true) }
    var pagiEnabled by remember { mutableStateOf(true) }
    var petangEnabled by remember { mutableStateOf(true) }

    // Fetch prayer times when screen launches
    LaunchedEffect(Unit) {
        viewModel.fetchTimings()
    }

    // State untuk menampilkan/menyembunyikan jam animasi
    var showClock by remember { mutableStateOf(false) }

    // Waktu real-time menggunakan Calendar
    val calendar = Calendar.getInstance()
    val hours = (calendar.get(Calendar.HOUR) + calendar.get(Calendar.MINUTE) / 60f).toFloat()
    val minutes = (calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND) / 60f).toFloat()
    val seconds = calendar.get(Calendar.SECOND).toFloat()

    // Animasi state
    val hourRotation = remember { Animatable(0f) }
    val minuteRotation = remember { Animatable(0f) }
    val secondRotation = remember { Animatable(0f) }

    // Hitung sudut awal untuk animasi
    LaunchedEffect(Unit) {
        hourRotation.snapTo((hours / 12) * 360)
        minuteRotation.snapTo((minutes / 60) * 360)
        secondRotation.snapTo((seconds / 60) * 360)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Background putih untuk konsistensi
            .padding(16.dp)
    ) {
        // Tombol Back
        IconButton(
            onClick = {
                (context as? androidx.activity.ComponentActivity)?.finish()
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(40.dp)
                .clip(MaterialTheme.shapes.small)
                .background(Color.White.copy(alpha = 0.9f))
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Kembali",
                tint = Color(0xFF2E7D32), // Hijau sesuai tema
                modifier = Modifier.size(24.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp), // Tambah padding biar nggak ketutup tombol Back
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showClock = !showClock
                            println("DEBUG: Klik Reminder Ibadah, showClock: $showClock")
                        }
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (showClock) Color(0xFFE8F5E9) else Color.White
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Reminder Ibadah",
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color(0xFF4CAF50),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            if (!showClock) {
                                Text(
                                    text = "Klik untuk lihat jam",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }

            if (showClock) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(8.dp)
                    ) {
                        ClockAnimation(
                            hourRotation = hourRotation,
                            minuteRotation = minuteRotation,
                            secondRotation = secondRotation,
                            onAnimationEnd = {
                                showClock = false
                                println("DEBUG: Animasi selesai, showClock set to false")
                            }
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Waktu Sholat",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                ReminderItem(
                    title = "Subuh ($fajrTime)",
                    isChecked = fajrEnabled,
                    onCheckedChange = {
                        fajrEnabled = it
                        viewModel.setFajrEnabled(it)
                    }
                )
            }

            item {
                ReminderItem(
                    title = "Dzuhur ($dhuhrTime)",
                    isChecked = dhuhrEnabled,
                    onCheckedChange = {
                        dhuhrEnabled = it
                        viewModel.setDhuhrEnabled(it)
                    }
                )
            }

            item {
                ReminderItem(
                    title = "Ashar ($asrTime)",
                    isChecked = asrEnabled,
                    onCheckedChange = {
                        asrEnabled = it
                        viewModel.setAsrEnabled(it)
                    }
                )
            }

            item {
                ReminderItem(
                    title = "Maghrib ($maghribTime)",
                    isChecked = maghribEnabled,
                    onCheckedChange = {
                        maghribEnabled = it
                        viewModel.setMaghribEnabled(it)
                    }
                )
            }

            item {
                ReminderItem(
                    title = "Isya ($ishaTime)",
                    isChecked = ishaEnabled,
                    onCheckedChange = {
                        ishaEnabled = it
                        viewModel.setIshaEnabled(it)
                    }
                )
            }

            item {
                Text(
                    text = "Waktu Dzikir",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                ReminderItem(
                    title = "Dzikir Pagi ($dzikirPagiTime)",
                    isChecked = pagiEnabled,
                    onCheckedChange = {
                        pagiEnabled = it
                        viewModel.setDzikirPagiEnabled(it)
                    }
                )
            }

            item {
                ReminderItem(
                    title = "Dzikir Petang ($dzikirPetangTime)",
                    isChecked = petangEnabled,
                    onCheckedChange = {
                        petangEnabled = it
                        viewModel.setDzikirPetangEnabled(it)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.fetchTimings() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Text("Refresh Jadwal")
                }
            }
        }
    }
}

@Composable
fun ClockAnimation(
    hourRotation: Animatable<Float, AnimationVector1D>,
    minuteRotation: Animatable<Float, AnimationVector1D>,
    secondRotation: Animatable<Float, AnimationVector1D>,
    onAnimationEnd: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        while (true) {
            val currentCalendar = Calendar.getInstance()
            val hours = (currentCalendar.get(Calendar.HOUR) + currentCalendar.get(Calendar.MINUTE) / 60f).toFloat()
            val minutes = (currentCalendar.get(Calendar.MINUTE) + currentCalendar.get(Calendar.SECOND) / 60f).toFloat()
            val seconds = currentCalendar.get(Calendar.SECOND).toFloat()

            hourRotation.snapTo((hours / 12) * 360)
            minuteRotation.snapTo((minutes / 60) * 360)
            secondRotation.snapTo((seconds / 60) * 360)

            delay(1000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(250.dp)) {
            val radius = size.minDimension / 2
            val center = Offset(size.width / 2, size.height / 2)

            drawCircle(
                color = Color.White,
                radius = radius,
                center = center
            )

            for (i in 1..12) {
                val angle = (i - 3) * 30 * PI.toFloat() / 180
                val x = center.x + cos(angle) * (radius * 0.8f)
                val y = center.y + sin(angle) * (radius * 0.8f)
                drawContext.canvas.nativeCanvas.drawText(
                    i.toString(),
                    x,
                    y,
                    android.graphics.Paint().apply {
                        textSize = 30f
                        textAlign = android.graphics.Paint.Align.CENTER
                        color = android.graphics.Color.BLACK
                    }
                )
            }

            rotate(hourRotation.value, pivot = center) {
                drawLine(
                    color = Color(0xFF4CAF50),
                    start = center,
                    end = Offset(center.x, center.y - radius * 0.5f),
                    strokeWidth = 6f
                )
            }

            rotate(minuteRotation.value, pivot = center) {
                drawLine(
                    color = Color(0xFF4CAF50),
                    start = center,
                    end = Offset(center.x, center.y - radius * 0.7f),
                    strokeWidth = 4f
                )
            }

            rotate(secondRotation.value, pivot = center) {
                drawLine(
                    color = Color(0xFF4CAF50),
                    start = center,
                    end = Offset(center.x, center.y - radius * 0.9f),
                    strokeWidth = 2f
                )
            }

            drawCircle(
                color = Color(0xFF4CAF50),
                radius = 10f,
                center = center
            )
        }
    }
}