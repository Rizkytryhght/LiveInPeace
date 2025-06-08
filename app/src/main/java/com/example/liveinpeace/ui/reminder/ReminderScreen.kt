package com.example.liveinpeace.ui.reminder

import androidx.compose.animation.core.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

    // Animasi untuk card expansion
    val cardHeight by animateDpAsState(
        targetValue = if (showClock) 420.dp else 80.dp,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "cardHeight"
    )

    // Animasi untuk rotasi icon
    val iconRotation by animateFloatAsState(
        targetValue = if (showClock) 360f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "iconRotation"
    )

    // Waktu real-time menggunakan Calendar
    val calendar = Calendar.getInstance()
    val hours = (calendar.get(Calendar.HOUR) + calendar.get(Calendar.MINUTE) / 60f).toFloat()
    val minutes = (calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND) / 60f).toFloat()
    val seconds = calendar.get(Calendar.SECOND).toFloat()

    // Animasi state untuk jam
    val hourRotation = remember { Animatable(0f) }
    val minuteRotation = remember { Animatable(0f) }
    val secondRotation = remember { Animatable(0f) }

    // Hitung sudut awal untuk animasi jam
    LaunchedEffect(Unit) {
        hourRotation.snapTo((hours / 12) * 360)
        minuteRotation.snapTo((minutes / 60) * 360)
        secondRotation.snapTo((seconds / 60) * 360)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2E7D32),
                        Color(0xFF388E3C),
                        Color(0xFF4CAF50)
                    )
                )
            )
            .padding(16.dp)
    ) {
        // Tombol Back dengan animasi hover
        var backButtonPressed by remember { mutableStateOf(false) }
        val backButtonScale by animateFloatAsState(
            targetValue = if (backButtonPressed) 0.9f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "backButtonScale"
        )

        IconButton(
            onClick = {
                (context as? androidx.activity.ComponentActivity)?.finish()
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(48.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.95f))
                .clickable(
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    indication = null
                ) {
                    backButtonPressed = !backButtonPressed
                }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = Color(0xFF2E7D32),
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer(scaleX = backButtonScale, scaleY = backButtonScale)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                // Card utama dengan animasi
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cardHeight)
                        .shadow(12.dp, RoundedCornerShape(20.dp))
                        .clickable {
                            showClock = !showClock
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header dengan icon dan teks
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Reminder Ibadah",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF2E7D32)
                                )
                                Text(
                                    text = if (showClock) "Waktu Sekarang" else "",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            // Icon dengan animasi rotasi dan garis indikator
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Icon dengan rotasi
                                Icon(
                                    imageVector = if (showClock) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (showClock) "Tutup" else "Buka",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier
                                        .size(32.dp)
                                        .rotate(iconRotation)
                                        .padding(4.dp)
                                )
                            }
                        }

                        // Jam animasi jika showClock true
                        if (showClock) {
                            Spacer(modifier = Modifier.height(16.dp))
                            ClockAnimation(
                                hourRotation = hourRotation,
                                minuteRotation = minuteRotation,
                                secondRotation = secondRotation
                            )
                        }
                    }
                }
            }

            item {
                // Section header dengan desain modern
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(9.dp)
                ) {
                    Text(
                        text = "⏰ Waktu Sholat",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            item {
                EnhancedReminderItem(
                    title = "Subuh",
                    time = fajrTime,
                    isChecked = fajrEnabled,
                    onCheckedChange = {
                        fajrEnabled = it
                        viewModel.setFajrEnabled(it)
                    },
                    icon = "🌅"
                )
            }

            item {
                EnhancedReminderItem(
                    title = "Dzuhur",
                    time = dhuhrTime,
                    isChecked = dhuhrEnabled,
                    onCheckedChange = {
                        dhuhrEnabled = it
                        viewModel.setDhuhrEnabled(it)
                    },
                    icon = "☀️"
                )
            }

            item {
                EnhancedReminderItem(
                    title = "Ashar",
                    time = asrTime,
                    isChecked = asrEnabled,
                    onCheckedChange = {
                        asrEnabled = it
                        viewModel.setAsrEnabled(it)
                    },
                    icon = "🌤️"
                )
            }

            item {
                EnhancedReminderItem(
                    title = "Maghrib",
                    time = maghribTime,
                    isChecked = maghribEnabled,
                    onCheckedChange = {
                        maghribEnabled = it
                        viewModel.setMaghribEnabled(it)
                    },
                    icon = "🌅"
                )
            }

            item {
                EnhancedReminderItem(
                    title = "Isya",
                    time = ishaTime,
                    isChecked = ishaEnabled,
                    onCheckedChange = {
                        ishaEnabled = it
                        viewModel.setIshaEnabled(it)
                    },
                    icon = "🌙"
                )
            }

            item {
                // Section header untuk dzikir
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "📿 Waktu Dzikir",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            item {
                EnhancedReminderItem(
                    title = "Dzikir Pagi",
                    time = dzikirPagiTime,
                    isChecked = pagiEnabled,
                    onCheckedChange = {
                        pagiEnabled = it
                        viewModel.setDzikirPagiEnabled(it)
                    },
                    icon = "🌅"
                )
            }

            item {
                EnhancedReminderItem(
                    title = "Dzikir Petang",
                    time = dzikirPetangTime,
                    isChecked = petangEnabled,
                    onCheckedChange = {
                        petangEnabled = it
                        viewModel.setDzikirPetangEnabled(it)
                    },
                    icon = "🌇"
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))

                // Tombol refresh dengan animasi
                var refreshPressed by remember { mutableStateOf(false) }
                val refreshScale by animateFloatAsState(
                    targetValue = if (refreshPressed) 0.95f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "refreshScale"
                )

                Button(
                    onClick = {
                        refreshPressed = true
                        viewModel.fetchTimings()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp))
                        .graphicsLayer(scaleX = refreshScale, scaleY = refreshScale),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF2E7D32)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "🔄 Refresh Jadwal",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                LaunchedEffect(refreshPressed) {
                    if (refreshPressed) {
                        delay(150)
                        refreshPressed = false
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedReminderItem(
    title: String,
    time: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: String
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "itemScale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isChecked) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.7f),
        animationSpec = tween(300),
        label = "itemBackground"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (isChecked) 8.dp else 4.dp, RoundedCornerShape(16.dp))
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null
            ) {
                isPressed = true
                onCheckedChange(!isChecked)
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = icon,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color(0xFF2E7D32)
                    )
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF4CAF50),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.Gray
                )
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}

@Composable
fun ClockAnimation(
    hourRotation: Animatable<Float, AnimationVector1D>,
    minuteRotation: Animatable<Float, AnimationVector1D>,
    secondRotation: Animatable<Float, AnimationVector1D>
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
            .height(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(220.dp)) {
            val radius = size.minDimension / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Background circle dengan gradient
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFF5F5F5)
                    ),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )

            // Border jam
            drawCircle(
                color = Color(0xFF4CAF50),
                radius = radius,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
            )

            // Angka jam dengan styling lebih baik
            for (i in 1..12) {
                val angle = (i - 3) * 30 * PI.toFloat() / 180
                val x = center.x + cos(angle) * (radius * 0.8f)
                val y = center.y + sin(angle) * (radius * 0.8f)
                drawContext.canvas.nativeCanvas.drawText(
                    i.toString(),
                    x,
                    y + 10f,
                    android.graphics.Paint().apply {
                        textSize = 28f
                        textAlign = android.graphics.Paint.Align.CENTER
                        color = android.graphics.Color.parseColor("#2E7D32")
                        isFakeBoldText = true
                    }
                )
            }

            // Titik jam
            for (i in 0..59) {
                val angle = i * 6 * PI.toFloat() / 180
                val isHour = i % 5 == 0
                val innerRadius = if (isHour) radius * 0.9f else radius * 0.95f
                val outerRadius = radius * 0.98f

                val innerX = center.x + cos(angle) * innerRadius
                val innerY = center.y + sin(angle) * innerRadius
                val outerX = center.x + cos(angle) * outerRadius
                val outerY = center.y + sin(angle) * outerRadius

                drawLine(
                    color = if (isHour) Color(0xFF4CAF50) else Color.Gray.copy(alpha = 0.5f),
                    start = Offset(innerX, innerY),
                    end = Offset(outerX, outerY),
                    strokeWidth = if (isHour) 3f else 1f
                )
            }

            // Jarum jam
            rotate(hourRotation.value, pivot = center) {
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF2E7D32), Color(0xFF4CAF50))
                    ),
                    start = center,
                    end = Offset(center.x, center.y - radius * 0.5f),
                    strokeWidth = 8f,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }

            // Jarum menit
            rotate(minuteRotation.value, pivot = center) {
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF2E7D32), Color(0xFF4CAF50))
                    ),
                    start = center,
                    end = Offset(center.x, center.y - radius * 0.7f),
                    strokeWidth = 6f,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }

            // Jarum detik
            rotate(secondRotation.value, pivot = center) {
                drawLine(
                    color = Color(0xFFFF5722),
                    start = center,
                    end = Offset(center.x, center.y - radius * 0.85f),
                    strokeWidth = 2f,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }

            // Pusat jam
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))
                ),
                radius = 12f,
                center = center
            )
        }
    }
}