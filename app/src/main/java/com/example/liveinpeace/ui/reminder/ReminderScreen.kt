package com.example.liveinpeace.ui.reminder

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    val hourRotation = remember { Animatable(0f) }
    val minuteRotation = remember { Animatable(0f) }
    val secondRotation = remember { Animatable(0f) }

    // Waktu real-time menggunakan Calendar
    val calendar = Calendar.getInstance()
    val hours = (calendar.get(Calendar.HOUR) + calendar.get(Calendar.MINUTE) / 60f).toFloat()
    val minutes = (calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND) / 60f).toFloat()
    val seconds = calendar.get(Calendar.SECOND).toFloat()

    // Hitung sudut awal untuk animasi
    LaunchedEffect(Unit) {
        hourRotation.snapTo((hours / 12) * 360)
        minuteRotation.snapTo((minutes / 60) * 360)
        secondRotation.snapTo((seconds / 60) * 360)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showClock = true }
            ) {
                Text(
                    text = "Reminder Ibadah",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        // Tampilkan jam animasi saat card diklik
        if (showClock) {
            item {
                ClockAnimation(
                    hourRotation = hourRotation,
                    minuteRotation = minuteRotation,
                    secondRotation = secondRotation,
                    onAnimationEnd = { showClock = false }
                )
            }
        }

        // Prayer reminders
        item {
            Text(
                text = "Waktu Sholat",
                style = MaterialTheme.typography.titleMedium,
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

        // Dzikir reminders section
        item {
            Text(
                text = "Waktu Dzikir",
                style = MaterialTheme.typography.titleMedium,
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
                    .padding(bottom = 16.dp)
            ) {
                Text("Refresh Jadwal")
            }
        }
    }
}

@Composable
fun ClockAnimation(
    hourRotation: Animatable<Float, AnimationVector1D>,
    minuteRotation: Animatable<Float, AnimationVector1D>,
    secondRotation: Animatable<Float, AnimationVector1D>,
    onAnimationEnd: () -> Unit
) {
    var animationStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!animationStarted) {
            // Waktu real-time menggunakan Calendar
            val calendar = Calendar.getInstance()
            val targetHours = (calendar.get(Calendar.HOUR) + calendar.get(Calendar.MINUTE) / 60f).toFloat()
            val targetMinutes = (calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND) / 60f).toFloat()
            val targetSeconds = calendar.get(Calendar.SECOND).toFloat()

            // Animasi dari 0 ke posisi target
            hourRotation.animateTo(
                targetValue = (targetHours * 30),
                animationSpec = tween(durationMillis = 2000)
            )
            minuteRotation.animateTo(
                targetValue = (targetMinutes * 6),
                animationSpec = tween(durationMillis = 2000)
            )
            secondRotation.animateTo(
                targetValue = (targetSeconds * 6),
                animationSpec = tween(durationMillis = 2000)
            )
            animationStarted = true
            delay(2000) // Tunggu animasi selesai (2 detik)
            onAnimationEnd()
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

            // Gambar lingkaran jam
            drawCircle(
                color = Color.LightGray,
                radius = radius,
                center = center
            )

            // Gambar angka (sederhana)
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

            // Gambar jarum jam
            rotate(hourRotation.value, pivot = center) {
                drawLine(
                    color = Color.Black,
                    start = center,
                    end = Offset(center.x, center.y - radius * 0.5f),
                    strokeWidth = 6f
                )
            }

            // Gambar jarum menit
            rotate(minuteRotation.value, pivot = center) {
                drawLine(
                    color = Color.Black,
                    start = center,
                    end = Offset(center.x, center.y - radius * 0.7f),
                    strokeWidth = 4f
                )
            }

            // Gambar jarum detik
            rotate(secondRotation.value, pivot = center) {
                drawLine(
                    color = Color.Red,
                    start = center,
                    end = Offset(center.x, center.y - radius * 0.9f),
                    strokeWidth = 2f
                )
            }

            // Gambar titik tengah
            drawCircle(
                color = Color.Black,
                radius = 10f,
                center = center
            )
        }
    }
}

//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.liveinpeace.viewModel.ReminderViewModel
//
//@Composable
//fun ReminderScreen() {
//    val viewModel: ReminderViewModel = viewModel()
//
//    // Collect prayer times
//    val fajrTime by viewModel.fajrTime.collectAsState()
//    val dhuhrTime by viewModel.dhuhrTime.collectAsState()
//    val asrTime by viewModel.asrTime.collectAsState()
//    val maghribTime by viewModel.maghribTime.collectAsState()
//    val ishaTime by viewModel.ishaTime.collectAsState()
//
//    // For legacy support
//    val dzikirPagiTime by viewModel.dzikirPagiTime.collectAsState()
//    val dzikirPetangTime by viewModel.dzikirPetangTime.collectAsState()
//
//    // Track reminder states
//    var fajrEnabled by remember { mutableStateOf(true) }
//    var dhuhrEnabled by remember { mutableStateOf(true) }
//    var asrEnabled by remember { mutableStateOf(true) }
//    var maghribEnabled by remember { mutableStateOf(true) }
//    var ishaEnabled by remember { mutableStateOf(true) }
//    var pagiEnabled by remember { mutableStateOf(true) }
//    var petangEnabled by remember { mutableStateOf(true) }
//
//    // Fetch prayer times when screen launches
//    LaunchedEffect(Unit) {
//        viewModel.fetchTimings()
//    }
//
//    LazyColumn(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        item {
//            Text(
//                text = "Reminder Ibadah",
//                style = MaterialTheme.typography.headlineLarge,
//                modifier = Modifier.padding(bottom = 16.dp)
//            )
//        }
//
//        // Prayer reminders
//        item {
//            Text(
//                text = "Waktu Sholat",
//                style = MaterialTheme.typography.titleMedium,
//                modifier = Modifier.padding(vertical = 8.dp)
//            )
//        }
//
//        item {
//            ReminderItem(
//                title = "Subuh ($fajrTime)",
//                isChecked = fajrEnabled,
//                onCheckedChange = {
//                    fajrEnabled = it
//                    viewModel.setFajrEnabled(it)
//                }
//            )
//        }
//
//        item {
//            ReminderItem(
//                title = "Dzuhur ($dhuhrTime)",
//                isChecked = dhuhrEnabled,
//                onCheckedChange = {
//                    dhuhrEnabled = it
//                    viewModel.setDhuhrEnabled(it)
//                }
//            )
//        }
//
//        item {
//            ReminderItem(
//                title = "Ashar ($asrTime)",
//                isChecked = asrEnabled,
//                onCheckedChange = {
//                    asrEnabled = it
//                    viewModel.setAsrEnabled(it)
//                }
//            )
//        }
//
//        item {
//            ReminderItem(
//                title = "Maghrib ($maghribTime)",
//                isChecked = maghribEnabled,
//                onCheckedChange = {
//                    maghribEnabled = it
//                    viewModel.setMaghribEnabled(it)
//                }
//            )
//        }
//
//        item {
//            ReminderItem(
//                title = "Isya ($ishaTime)",
//                isChecked = ishaEnabled,
//                onCheckedChange = {
//                    ishaEnabled = it
//                    viewModel.setIshaEnabled(it)
//                }
//            )
//        }
//
//        // Dzikir reminders section
//        item {
//            Text(
//                text = "Waktu Dzikir",
//                style = MaterialTheme.typography.titleMedium,
//                modifier = Modifier.padding(vertical = 8.dp)
//            )
//        }
//
//        item {
//            ReminderItem(
//                title = "Dzikir Pagi ($dzikirPagiTime)",
//                isChecked = pagiEnabled,
//                onCheckedChange = {
//                    pagiEnabled = it
//                    viewModel.setDzikirPagiEnabled(it)
//                }
//            )
//        }
//
//        item {
//            ReminderItem(
//                title = "Dzikir Petang ($dzikirPetangTime)",
//                isChecked = petangEnabled,
//                onCheckedChange = {
//                    petangEnabled = it
//                    viewModel.setDzikirPetangEnabled(it)
//                }
//            )
//        }
//
//        item {
//            Spacer(modifier = Modifier.height(16.dp))
//            Button(
//                onClick = { viewModel.fetchTimings() },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 16.dp)
//            ) {
//                Text("Refresh Jadwal")
//            }
//        }
//    }
//}