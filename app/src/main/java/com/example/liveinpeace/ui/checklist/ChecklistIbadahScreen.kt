package com.example.liveinpeace.ui.checklist

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liveinpeace.ui.features.FeaturesListActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

data class PrayerState(
    var tepat: Boolean = false,
    var qadha: Boolean = false,
    var badiyah: Boolean = false
)

data class SunnahState(
    var dhuha: Boolean = false,
    var tahajud: Boolean = false,
    var rawatib: Boolean = false,
    var puasaSunnah: Boolean = false,
    var dzikirPagi: Boolean = false,
    var dzikirPetang: Boolean = false,
    var shalawat: Boolean = false,
    var istighfar: Boolean = false,
    var muhasabah: Boolean = false,
    var hafalan: Boolean = false
)

data class TextInputState(
    var quranFrom: String = "",
    var quranTo: String = "",
    var harapan: String = "",
    var syukur: String = ""
)

sealed class ChecklistState {
    data object Loading : ChecklistState()
    data class Success(
        val prayerState: Map<String, PrayerState>,
        val sunnahState: SunnahState,
        val textInputState: TextInputState
    ) : ChecklistState()
    data class Error(val message: String) : ChecklistState()
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun ChecklistIbadahScreen(
    firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    todayDate: String
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displayFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
    var selectedDate by remember { mutableStateOf(todayDate) }
    var checklistState by remember { mutableStateOf<ChecklistState>(ChecklistState.Loading) }

    LaunchedEffect(selectedDate) {
        checklistState = ChecklistState.Loading
        val uid = auth.currentUser?.uid ?: run {
            checklistState = ChecklistState.Error("User tidak terautentikasi")
            Log.e("ChecklistIbadahScreen", "User tidak terautentikasi")
            return@LaunchedEffect
        }
        try {
            Log.d("ChecklistIbadahScreen", "Memuat data untuk tanggal: $selectedDate")
            val docRef = firestore.collection("users").document(uid)
                .collection("checklists").document(selectedDate)
            val doc = docRef.get().await()
            if (doc.exists()) {
                val prayerState = mapOf(
                    "Subuh" to PrayerState(
                        tepat = doc.getBoolean("subuh_tepat") ?: false,
                        qadha = doc.getBoolean("subuh_qadha") ?: false,
                        badiyah = doc.getBoolean("subuh_badiyah") ?: false
                    ),
                    "Dzuhur" to PrayerState(
                        tepat = doc.getBoolean("dzuhur_tepat") ?: false,
                        qadha = doc.getBoolean("dzuhur_qadha") ?: false,
                        badiyah = doc.getBoolean("dzuhur_badiyah") ?: false
                    ),
                    "Ashar" to PrayerState(
                        tepat = doc.getBoolean("ashar_tepat") ?: false,
                        qadha = doc.getBoolean("ashar_qadha") ?: false,
                        badiyah = doc.getBoolean("ashar_badiyah") ?: false
                    ),
                    "Maghrib" to PrayerState(
                        tepat = doc.getBoolean("maghrib_tepat") ?: false,
                        qadha = doc.getBoolean("maghrib_qadha") ?: false,
                        badiyah = doc.getBoolean("maghrib_badiyah") ?: false
                    ),
                    "Isya" to PrayerState(
                        tepat = doc.getBoolean("isya_tepat") ?: false,
                        qadha = doc.getBoolean("isya_qadha") ?: false,
                        badiyah = doc.getBoolean("isya_badiyah") ?: false
                    )
                )
                val sunnahState = SunnahState(
                    dhuha = doc.getBoolean("dhuha") ?: false,
                    tahajud = doc.getBoolean("tahajud") ?: false,
                    rawatib = doc.getBoolean("rawatib") ?: false,
                    puasaSunnah = doc.getBoolean("puasa_sunnah") ?: false,
                    dzikirPagi = doc.getBoolean("dzikir_pagi") ?: false,
                    dzikirPetang = doc.getBoolean("dzikir_petang") ?: false,
                    shalawat = doc.getBoolean("shalawat") ?: false,
                    istighfar = doc.getBoolean("istighfar") ?: false,
                    muhasabah = doc.getBoolean("muhasabah") ?: false,
                    hafalan = doc.getBoolean("hafalan") ?: false
                )
                val textInputState = TextInputState(
                    quranFrom = doc.getString("quran_from") ?: "",
                    quranTo = doc.getString("quran_to") ?: "",
                    harapan = doc.getString("harapan") ?: "",
                    syukur = doc.getString("syukur") ?: ""
                )
                checklistState = ChecklistState.Success(prayerState, sunnahState, textInputState)
                Log.d("ChecklistIbadahScreen", "Data berhasil dimuat")
            } else {
                checklistState = ChecklistState.Success(
                    prayerState = mapOf(
                        "Subuh" to PrayerState(),
                        "Dzuhur" to PrayerState(),
                        "Ashar" to PrayerState(),
                        "Maghrib" to PrayerState(),
                        "Isya" to PrayerState()
                    ),
                    sunnahState = SunnahState(),
                    textInputState = TextInputState()
                )
                Log.d("ChecklistIbadahScreen", "Dokumen tidak ada, inisialisasi data kosong")
            }
        } catch (e: Exception) {
            checklistState = ChecklistState.Error("Gagal memuat data: ${e.message}")
            Log.e("ChecklistIbadahScreen", "Error memuat data: ${e.message}", e)
        }
    }

    fun saveChecklist(
        prayerState: Map<String, PrayerState>,
        sunnahState: SunnahState,
        textInputState: TextInputState
    ) {
        val uid = auth.currentUser?.uid ?: run {
            Toast.makeText(context, "User tidak terautentikasi", Toast.LENGTH_SHORT).show()
            Log.e("ChecklistIbadahScreen", "Gagal menyimpan: User tidak terautentikasi")
            return
        }
        val checklistData = hashMapOf<String, Any>(
            "date" to selectedDate
        ).apply {
            prayerState.forEach { (prayer, state) ->
                put("${prayer.lowercase()}_tepat", state.tepat)
                put("${prayer.lowercase()}_qadha", state.qadha)
                put("${prayer.lowercase()}_badiyah", state.badiyah)
            }
            put("dhuha", sunnahState.dhuha)
            put("tahajud", sunnahState.tahajud)
            put("rawatib", sunnahState.rawatib)
            put("puasa_sunnah", sunnahState.puasaSunnah)
            put("dzikir_pagi", sunnahState.dzikirPagi)
            put("dzikir_petang", sunnahState.dzikirPetang)
            put("shalawat", sunnahState.shalawat)
            put("istighfar", sunnahState.istighfar)
            put("muhasabah", sunnahState.muhasabah)
            put("hafalan", sunnahState.hafalan)
            put("quran_from", textInputState.quranFrom)
            put("quran_to", textInputState.quranTo)
            put("harapan", textInputState.harapan)
            put("syukur", textInputState.syukur)
        }

        Log.d("ChecklistIbadahScreen", "Menyimpan data: $checklistData")
        firestore.collection("users").document(uid)
            .collection("checklists").document(selectedDate)
            .set(checklistData)
            .addOnSuccessListener {
                val date = dateFormat.parse(selectedDate)?.let { displayFormat.format(it) } ?: selectedDate
                Toast.makeText(context, "Checklist ibadah tanggal $date disimpan", Toast.LENGTH_SHORT).show()
                Log.d("ChecklistIbadahScreen", "Checklist disimpan untuk tanggal $date")
                if (selectedDate == todayDate) {
                    val intent = Intent(context, FeaturesListActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    (context as? ChecklistIbadahActivity)?.finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Gagal menyimpan checklist", Toast.LENGTH_SHORT).show()
                Log.e("ChecklistIbadahScreen", "Gagal menyimpan checklist: ${e.message}", e)
            }
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF2E7D32),
            onPrimary = Color.White,
            surface = Color.White,
            onSurface = Color(0xFF1B5E20),
            background = Color.White,
            onBackground = Color(0xFF1B5E20),
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            when (checklistState) {
                is ChecklistState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                is ChecklistState.Success -> {
                    val state = checklistState as ChecklistState.Success
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(12.dp)),
                                tonalElevation = 4.dp
                            ) {
                                Header(
                                    selectedDate = selectedDate,
                                    todayDate = todayDate,
                                    dateFormat = dateFormat,
                                    displayFormat = displayFormat,
                                    onBackClick = { (context as? ChecklistIbadahActivity)?.finish() }
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(12.dp)),
                                tonalElevation = 4.dp
                            ) {
                                DatePickerSection(
                                    selectedDate = selectedDate,
                                    onDateSelected = { selectedDate = it },
                                    dateFormat = dateFormat,
                                    displayFormat = displayFormat
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFF2E7D32),
                                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                                )
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(12.dp)),
                                tonalElevation = 4.dp
                            ) {
                                PrayerSection(
                                    prayerState = state.prayerState,
                                    onPrayerStateChange = { prayer, newState ->
                                        checklistState = ChecklistState.Success(
                                            prayerState = state.prayerState.toMutableMap().apply { put(prayer, newState) },
                                            sunnahState = state.sunnahState,
                                            textInputState = state.textInputState
                                        )
                                    }
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(12.dp)),
                                tonalElevation = 4.dp
                            ) {
                                QuranSection(
                                    quranFrom = state.textInputState.quranFrom,
                                    quranTo = state.textInputState.quranTo,
                                    onQuranFromChange = {
                                        checklistState = ChecklistState.Success(
                                            prayerState = state.prayerState,
                                            sunnahState = state.sunnahState,
                                            textInputState = state.textInputState.copy(quranFrom = it)
                                        )
                                    },
                                    onQuranToChange = {
                                        checklistState = ChecklistState.Success(
                                            prayerState = state.prayerState,
                                            sunnahState = state.sunnahState,
                                            textInputState = state.textInputState.copy(quranTo = it)
                                        )
                                    }
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(12.dp)),
                                tonalElevation = 4.dp
                            ) {
                                HarapanSection(
                                    harapan = state.textInputState.harapan,
                                    onHarapanChange = {
                                        checklistState = ChecklistState.Success(
                                            prayerState = state.prayerState,
                                            sunnahState = state.sunnahState,
                                            textInputState = state.textInputState.copy(harapan = it)
                                        )
                                    }
                                )
                            }

                            HorizontalDivider(color = Color.White)

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(12.dp)),
                                tonalElevation = 4.dp
                            ) {
                                SunnahSection(
                                    sunnahState = state.sunnahState,
                                    onSunnahStateChange = {
                                        checklistState = ChecklistState.Success(
                                            prayerState = state.prayerState,
                                            sunnahState = it,
                                            textInputState = state.textInputState
                                        )
                                    },
                                    isLoaded = true
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(12.dp)),
                                tonalElevation = 4.dp
                            ) {
                                SyukurSection(
                                    syukur = state.textInputState.syukur,
                                    onSyukurChange = {
                                        checklistState = ChecklistState.Success(
                                            prayerState = state.prayerState,
                                            sunnahState = state.sunnahState,
                                            textInputState = state.textInputState.copy(syukur = it)
                                        )
                                    })
                            }

                            Button(
                                onClick = {
                                    saveChecklist(
                                        state.prayerState,
                                        state.sunnahState,
                                        state.textInputState
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Simpan",
                                    color = Color(0xFF2E7D32),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                is ChecklistState.Error -> {
                    Text(
                        modifier = Modifier.fillMaxSize(),
                        text = "Terjadi kesalahan, coba lagi nanti",
                        color = Color(0xFF1B5E20),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun Header(
    selectedDate: String,
    todayDate: String,
    dateFormat: SimpleDateFormat,
    displayFormat: SimpleDateFormat,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2E7D32), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() },
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = if (selectedDate == todayDate)
                "Sudahkah kamu beribadah?"
            else
                "Ibadah pada ${displayFormat.format(dateFormat.parse(selectedDate) ?: Date())}",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun DatePickerSection(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    dateFormat: SimpleDateFormat,
    displayFormat: SimpleDateFormat
) {
    val calendar = Calendar.getInstance()
    val currentDate = dateFormat.format(Date())
    calendar.time = dateFormat.parse(selectedDate) ?: Date()
    var currentMonth by remember { mutableStateOf(calendar.clone() as Calendar) }
    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()
    var animationOffset by remember { mutableFloatStateOf(0f) }
    val animatedOffset by animateFloatAsState(
        targetValue = animationOffset,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "calendarAnimation"
    )

    currentMonth.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfMonth = currentMonth.get(Calendar.DAY_OF_WEEK) - 1
    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (dragOffsetX < -100f) {
                            scope.launch {
                                animationOffset = -100f
                                currentMonth.add(Calendar.MONTH, 1)
                                currentMonth = currentMonth.clone() as Calendar
                                animationOffset = 0f
                            }
                        } else if (dragOffsetX > 100f) {
                            scope.launch {
                                animationOffset = 100f
                                currentMonth.add(Calendar.MONTH, -1)
                                currentMonth = currentMonth.clone() as Calendar
                                animationOffset = 0f
                            }
                        }
                        dragOffsetX = 0f
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        dragOffsetX += dragAmount
                        change.consume()
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .offset(x = animatedOffset.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous Month",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            scope.launch {
                                animationOffset = 100f
                                currentMonth.add(Calendar.MONTH, -1)
                                currentMonth = currentMonth.clone() as Calendar
                                animationOffset = 0f
                            }
                        },
                    tint = Color(0xFF2E7D32)
                )
                Text(
                    text = monthFormat.format(currentMonth.time),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    textAlign = TextAlign.Center
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next Month",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            scope.launch {
                                animationOffset = -100f
                                currentMonth.add(Calendar.MONTH, 1)
                                currentMonth = currentMonth.clone() as Calendar
                                animationOffset = 0f
                            }
                        },
                    tint = Color(0xFF2E7D32)
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("M", "S", "S", "R", "K", "J", "S").forEach { day ->
                    Text(
                        text = day,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            val totalSlots = (firstDayOfMonth + daysInMonth + 6) / 7 * 7
            Column {
                for (week in 0 until totalSlots / 7) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (dayOfWeek in 0 until 7) {
                            val dayIndex = week * 7 + dayOfWeek - firstDayOfMonth + 1
                            if (dayIndex in 1..daysInMonth) {
                                val tempCalendar = currentMonth.clone() as Calendar
                                tempCalendar.set(Calendar.DAY_OF_MONTH, dayIndex)
                                val dateStr = dateFormat.format(tempCalendar.time)
                                val isSelected = dateStr == selectedDate
                                val isToday = dateStr == currentDate

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) Color(0xFF2E7D32).copy(alpha = 0.1f)
                                            else Color.Transparent
                                        )
                                        .clickable {
                                            onDateSelected(dateStr)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isToday) {
                                        Canvas(modifier = Modifier.size(24.dp)) {
                                            drawCircle(
                                                color = Color(0xFF2E7D32),
                                                radius = size.minDimension / 2,
                                                style = Stroke(width = 2.dp.toPx())
                                            )
                                        }
                                    }
                                    Text(
                                        text = dayIndex.toString(),
                                        fontSize = 14.sp,
                                        color = if (isSelected) Color(0xFF2E7D32) else Color(0xFF1B5E20),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrayerSection(
    prayerState: Map<String, PrayerState>,
    onPrayerStateChange: (String, PrayerState) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFC8E6C9), RoundedCornerShape(8.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Shalat", "Tepat Waktu?", "Qadha/Ijab", "Ba'diyah").forEach {
                Text(
                    it,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        prayerState.forEach { (prayer, state) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = prayer,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    fontSize = 16.sp,
                    color = Color(0xFF2E7D32),
                    textAlign = TextAlign.Center
                )

                Checkbox(
                    checked = state.tepat,
                    onCheckedChange = { onPrayerStateChange(prayer, state.copy(tepat = it)) },
                    modifier = Modifier.weight(1f),
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2E7D32))
                )

                Checkbox(
                    checked = state.qadha,
                    onCheckedChange = { onPrayerStateChange(prayer, state.copy(qadha = it)) },
                    modifier = Modifier.weight(1f),
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2E7D32))
                )

                Checkbox(
                    checked = state.badiyah,
                    onCheckedChange = { onPrayerStateChange(prayer, state.copy(badiyah = it)) },
                    modifier = Modifier.weight(1f),
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2E7D32))
                )
            }
        }
    }
}

@Composable
fun QuranSection(
    quranFrom: String,
    quranTo: String,
    onQuranFromChange: (String) -> Unit,
    onQuranToChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Book,
                contentDescription = "Quran",
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF2E7D32)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Tilawah Al-Quran:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
        }

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Dari: ", modifier = Modifier.padding(end = 8.dp), color = Color(0xFF2E7D32))
            OutlinedTextField(
                value = quranFrom,
                onValueChange = onQuranFromChange,
                placeholder = { Text("Al-Baqarah ayat 1") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color(0xFF81C784),
                    cursorColor = Color(0xFF2E7D32),
                    focusedLabelColor = Color(0xFF2E7D32)
                )
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Sampai: ", modifier = Modifier.padding(end = 8.dp), color = Color(0xFF2E7D32))
            OutlinedTextField(
                value = quranTo,
                onValueChange = onQuranToChange,
                placeholder = { Text("Al-Baqarah ayat 10") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color(0xFF81C784),
                    cursorColor = Color(0xFF2E7D32),
                    focusedLabelColor = Color(0xFF2E7D32)
                )
            )
        }
    }
}

@Composable
fun HarapanSection(
    harapan: String,
    onHarapanChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "Harapan/Doa untuk hari esok:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = harapan,
            onValueChange = onHarapanChange,
            placeholder = { Text("Semoga Allah memberikan berkah...", color = Color(0xFF81C784)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2E7D32),
                unfocusedBorderColor = Color(0xFF81C784),
                cursorColor = Color(0xFF2E7D32),
                focusedLabelColor = Color(0xFF2E7D32)
            )
        )
    }
}

@Composable
fun SunnahSection(
    sunnahState: SunnahState,
    onSunnahStateChange: (SunnahState) -> Unit,
    isLoaded: Boolean
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "Ibadah Sunnah:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )
        Spacer(Modifier.height(8.dp))
        val sunnahItems: List<Pair<String, (Boolean) -> SunnahState>> = listOf(
            "Shalat Dhuha" to { value: Boolean -> sunnahState.copy(dhuha = value) },
            "Shalat Tahajud" to { value: Boolean -> sunnahState.copy(tahajud = value) },
            "Shalat Rawatib" to { value: Boolean -> sunnahState.copy(rawatib = value) },
            "Puasa Sunnah" to { value: Boolean -> sunnahState.copy(puasaSunnah = value) },
            "Dzikir" to { value: Boolean ->
                if (isLoaded) {
                    Toast.makeText(
                        context,
                        if (value) "Masya Allah! Dzikirnya jangan sampai ketinggalan ya!"
                        else "Yuk semangat mulai dzikir!",
                        Toast.LENGTH_LONG
                    ).show()
                }
                sunnahState.copy(dzikirPagi = value, dzikirPetang = value)
            },
            "Shalawat Nabi" to { value: Boolean -> sunnahState.copy(shalawat = value) },
            "Istighfar" to { value: Boolean -> sunnahState.copy(istighfar = value) },
            "Muhasabah" to { value: Boolean -> sunnahState.copy(muhasabah = value) },
            "Hafalan" to { value: Boolean -> sunnahState.copy(hafalan = value) }
        )

        sunnahItems.forEach { (name, updateState) ->
            val isChecked = when (name) {
                "Shalat Dhuha" -> sunnahState.dhuha
                "Shalat Tahajud" -> sunnahState.tahajud
                "Shalat Rawatib" -> sunnahState.rawatib
                "Puasa Sunnah" -> sunnahState.puasaSunnah
                "Dzikir" -> sunnahState.dzikirPagi || sunnahState.dzikirPetang
                "Shalawat Nabi" -> sunnahState.shalawat
                "Istighfar" -> sunnahState.istighfar
                "Muhasabah" -> sunnahState.muhasabah
                "Hafalan" -> sunnahState.hafalan
                else -> false
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    modifier = Modifier.weight(3f),
                    fontSize = 14.sp,
                    color = Color(0xFF2E7D32)
                )
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { newValue ->
                        onSunnahStateChange(updateState(newValue))
                    },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2E7D32))
                )
            }
        }
    }
}

@Composable
fun SyukurSection(
    syukur: String,
    onSyukurChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "Hari ini aku bersyukur karena:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = syukur,
            onValueChange = onSyukurChange,
            placeholder = {
                Text(
                    "Tulis rasa syukurmu hari ini...",
                    color = Color(0xFF81C784)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2E7D32),
                unfocusedBorderColor = Color(0xFF81C784),
                cursorColor = Color(0xFF2E7D32),
                focusedLabelColor = Color(0xFF2E7D32)
            )
        )
    }
}