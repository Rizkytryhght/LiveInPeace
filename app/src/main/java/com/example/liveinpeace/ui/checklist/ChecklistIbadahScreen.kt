package com.example.liveinpeace.ui.checklist

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liveinpeace.ui.features.FeaturesListActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.Composable

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

@Composable
fun ChecklistIbadahScreen() {
    val context = LocalContext.current
    val density = LocalDensity.current
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displayFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
    var selectedDate by remember { mutableStateOf(dateFormat.format(Date())) }
    var isLoaded by remember { mutableStateOf(false) }
    var prayerState by remember {
        mutableStateOf(
            mapOf(
                "Subuh" to PrayerState(),
                "Dzuhur" to PrayerState(),
                "Ashar" to PrayerState(),
                "Maghrib" to PrayerState(),
                "Isya" to PrayerState()
            )
        )
    }
    var sunnahState by remember { mutableStateOf(SunnahState()) }
    var textInputState by remember { mutableStateOf(TextInputState()) }
    var panelOffsetY by remember { mutableStateOf(480.dp) }
    val maxOffsetY = 480.dp
    val minOffsetY = 0.dp

    LaunchedEffect(selectedDate) {
        isLoaded = false
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        val docRef = firestore.collection("users").document(uid)
            .collection("checklists").document(selectedDate)

        docRef.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                prayerState = prayerState.mapValues { (prayer, state) ->
                    state.copy(
                        tepat = doc.getBoolean("${prayer.lowercase()}_tepat") ?: false,
                        qadha = doc.getBoolean("${prayer.lowercase()}_qadha") ?: false,
                        badiyah = doc.getBoolean("${prayer.lowercase()}_badiyah") ?: false
                    )
                }
                sunnahState = SunnahState(
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
                textInputState = TextInputState(
                    quranFrom = doc.getString("quran_from") ?: "",
                    quranTo = doc.getString("quran_to") ?: "",
                    harapan = doc.getString("harapan") ?: "",
                    syukur = doc.getString("syukur") ?: ""
                )
            } else {
                prayerState = prayerState.mapValues { (_, _) -> PrayerState() }
                sunnahState = SunnahState()
                textInputState = TextInputState()
            }
            isLoaded = true
        }.addOnFailureListener {
            Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            prayerState = prayerState.mapValues { (_, _) -> PrayerState() }
            sunnahState = SunnahState()
            textInputState = TextInputState()
            isLoaded = true
        }
    }

    fun saveChecklist() {
        val uid = auth.currentUser?.uid ?: return
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

        firestore.collection("users").document(uid)
            .collection("checklists").document(selectedDate)
            .set(checklistData)
            .addOnSuccessListener {
                val todayFormat = dateFormat.format(Date())
                if (selectedDate == todayFormat) {
                    Toast.makeText(context, "Checklist ibadah hari ini berhasil disimpan", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, FeaturesListActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    (context as? ChecklistIbadahActivity)?.finish()
                } else {
                    val date = dateFormat.parse(selectedDate)?.let { displayFormat.format(it) } ?: selectedDate
                    Toast.makeText(context, "Checklist ibadah tanggal $date berhasil disimpan", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Gagal menyimpan checklist", Toast.LENGTH_SHORT).show()
            }
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF4CAF50),
            onPrimary = Color.White,
            surface = Color(0xFFF1F8E9),
            onSurface = Color(0xFF1B5E20),
            background = Color(0xFFE8F5E9),
            onBackground = Color(0xFF1B5E20),
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFADD8E6)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F8E9), RoundedCornerShape(12.dp)),
                        tonalElevation = 4.dp
                    ) {
                        Header(
                            selectedDate = selectedDate,
                            dateFormat = dateFormat,
                            displayFormat = displayFormat,
                            onBackClick = { (context as? ChecklistIbadahActivity)?.finish() }
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F8E9), RoundedCornerShape(12.dp)),
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
                        .offset(y = panelOffsetY)
                        .background(
                            color = Color(0xFFF1F8E9),
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )

                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                val deltaDp = with(density) { delta.toDp() }
                                val newOffset = panelOffsetY + deltaDp
                                panelOffsetY = newOffset.coerceIn(minOffsetY, maxOffsetY)
                            }
                        ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp)
                            .height(4.dp)
                            .width(40.dp)
                            .background(Color.Gray, shape = RoundedCornerShape(2.dp))
                            .align(Alignment.CenterHorizontally)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F8E9), RoundedCornerShape(12.dp)),
                            tonalElevation = 4.dp
                        ) {
                            PrayerSection(
                                prayerState = prayerState,
                                onPrayerStateChange = { prayer, newState ->
                                    prayerState = prayerState.toMutableMap().apply { put(prayer, newState) }
                                }
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F8E9), RoundedCornerShape(12.dp)),
                            tonalElevation = 4.dp
                        ) {
                            QuranSection(
                                quranFrom = textInputState.quranFrom,
                                quranTo = textInputState.quranTo,
                                onQuranFromChange = {
                                    textInputState = textInputState.copy(quranFrom = it)
                                },
                                onQuranToChange = {
                                    textInputState = textInputState.copy(quranTo = it)
                                }
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F8E9), RoundedCornerShape(12.dp)),
                            tonalElevation = 4.dp
                        ) {
                            HarapanSection(
                                harapan = textInputState.harapan,
                                onHarapanChange = {
                                    textInputState = textInputState.copy(harapan = it)
                                }
                            )
                        }

                        Divider(color = Color(0xFFC5E1A5))

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F8E9), RoundedCornerShape(12.dp)),
                            tonalElevation = 4.dp
                        ) {
                            SunnahSection(
                                sunnahState = sunnahState,
                                onSunnahStateChange = { sunnahState = it },
                                isLoaded = isLoaded
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F8E9), RoundedCornerShape(12.dp)),
                            tonalElevation = 4.dp
                        ) {
                            SyukurSection(
                                syukur = textInputState.syukur,
                                onSyukurChange = {
                                    textInputState = textInputState.copy(syukur = it)
                                }
                            )
                        }

                        Button(
                            onClick = { saveChecklist() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F61)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Simpan", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Header(
    selectedDate: String,
    dateFormat: SimpleDateFormat,
    displayFormat: SimpleDateFormat,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() },
            tint = Color(0xFF1B5E20)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = if (selectedDate == dateFormat.format(Date()))
                "Sudahkah kamu beribadah hari ini?"
            else
                "Ibadah pada ${displayFormat.format(dateFormat.parse(selectedDate) ?: Date())}",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

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

    currentMonth.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfMonth = currentMonth.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday
    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFBBDEFB), RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous Month",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            currentMonth.add(Calendar.MONTH, -1)
                            currentMonth = currentMonth.clone() as Calendar
                        },
                    tint = Color(0xFF1B5E20)
                )
                Text(
                    text = monthFormat.format(currentMonth.time),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20),
                    textAlign = TextAlign.Center
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next Month",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            currentMonth.add(Calendar.MONTH, 1)
                            currentMonth = currentMonth.clone() as Calendar
                        },
                    tint = Color(0xFF1B5E20)
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
                        color = Color(0xFF1B5E20),
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
                                            if (isSelected) Color(0xFF4CAF50).copy(alpha = 0.3f)
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
                                                color = Color(0xFF4CAF50),
                                                radius = size.minDimension / 2,
                                                style = Stroke(width = 2.dp.toPx())
                                            )
                                        }
                                    }
                                    Text(
                                        text = dayIndex.toString(),
                                        fontSize = 14.sp,
                                        color = if (isSelected) Color(0xFF4CAF50) else Color(0xFF1B5E20),
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
                .background(Color(0xFFBBDEFB), RoundedCornerShape(8.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Shalat", "Tepat Waktu?", "Qadha/Ijab", "Ba'diyah").forEach {
                Text(
                    it,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20),
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
                    .background(Color(0xFFE0F7FA), RoundedCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = prayer,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    fontSize = 16.sp,
                    color = Color(0xFF1B5E20),
                    textAlign = TextAlign.Center
                )

                Checkbox(
                    checked = state.tepat,
                    onCheckedChange = { onPrayerStateChange(prayer, state.copy(tepat = it)) },
                    modifier = Modifier.weight(1f),
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50))
                )

                Checkbox(
                    checked = state.qadha,
                    onCheckedChange = { onPrayerStateChange(prayer, state.copy(qadha = it)) },
                    modifier = Modifier.weight(1f),
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50))
                )

                Checkbox(
                    checked = state.badiyah,
                    onCheckedChange = { onPrayerStateChange(prayer, state.copy(badiyah = it)) },
                    modifier = Modifier.weight(1f),
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50))
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
                tint = Color(0xFF1B5E20)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Tilawah Al-Quran:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )
        }

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Dari: ", modifier = Modifier.padding(end = 8.dp), color = Color(0xFF1B5E20))
            OutlinedTextField(
                value = quranFrom,
                onValueChange = onQuranFromChange,
                placeholder = { Text("Al-Baqarah ayat 1") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4CAF50),
                    unfocusedBorderColor = Color(0xFFB0BEC5),
                    cursorColor = Color(0xFF1B5E20),
                    focusedLabelColor = Color(0xFF4CAF50)
                )
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Sampai: ", modifier = Modifier.padding(end = 8.dp), color = Color(0xFF1B5E20))
            OutlinedTextField(
                value = quranTo,
                onValueChange = onQuranToChange,
                placeholder = { Text("Al-Baqarah ayat 10") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4CAF50),
                    unfocusedBorderColor = Color(0xFFB0BEC5),
                    cursorColor = Color(0xFF1B5E20),
                    focusedLabelColor = Color(0xFF4CAF50)
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
        Text("Harapan/Doa untuk hari esok:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = harapan,
            onValueChange = onHarapanChange,
            placeholder = { Text("Semoga Allah memberikan berkah...", color = Color(0xFFB0BEC5)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4CAF50),
                unfocusedBorderColor = Color(0xFFB0BEC5),
                cursorColor = Color(0xFF1B5E20),
                focusedLabelColor = Color(0xFF4CAF50)
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
        Text("Ibadah Sunnah:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
        Spacer(Modifier.height(8.dp))
        val sunnahItems: List<Pair<String, (Boolean) -> SunnahState>> = listOf(
            "Shalat Dhuha" to { value: Boolean -> sunnahState.copy(dhuha = value) },
            "Shalat Tahajud" to { value: Boolean -> sunnahState.copy(tahajud = value) },
            "Shalat Rawatib" to { value: Boolean -> sunnahState.copy(rawatib = value) },
            "Puasa Sunnah" to { value: Boolean -> sunnahState.copy(puasaSunnah = value) },
            "Dzikir Pagi" to { value: Boolean ->
                if (isLoaded) {
                    Toast.makeText(
                        context,
                        if (value) "Masya Allah! Dzikir paginya jangan sampai ketinggalan ya!"
                        else "Yuk semangat mulai dzikir pagi!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                sunnahState.copy(dzikirPagi = value)
            },
            "Dzikir Petang" to { value: Boolean ->
                if (isLoaded) {
                    Toast.makeText(
                        context,
                        if (value) "Masya Allah! Dzikir petang udah dilakukan~"
                        else "Dzikir petang jangan lupa yaa 🥹",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                sunnahState.copy(dzikirPetang = value)
            },
            "Shalawat Nabi" to { value: Boolean -> sunnahState.copy(shalawat = value) },
            "Istighfar" to { value: Boolean -> sunnahState.copy(istighfar = value) },
            "Muhasabah" to { value: Boolean -> sunnahState.copy(muhasabah = value) },
            "Hafalan Surah" to { value: Boolean -> sunnahState.copy(hafalan = value) }
        )

        sunnahItems.forEach { (name, updateState) ->
            val isChecked = when (name) {
                "Shalat Dhuha" -> sunnahState.dhuha
                "Shalat Tahajud" -> sunnahState.tahajud
                "Shalat Rawatib" -> sunnahState.rawatib
                "Puasa Sunnah" -> sunnahState.puasaSunnah
                "Dzikir Pagi" -> sunnahState.dzikirPagi
                "Dzikir Petang" -> sunnahState.dzikirPetang
                "Shalawat Nabi" -> sunnahState.shalawat
                "Istighfar" -> sunnahState.istighfar
                "Muhasabah" -> sunnahState.muhasabah
                "Hafalan Surah" -> sunnahState.hafalan
                else -> false
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(name, modifier = Modifier.weight(3f), fontSize = 14.sp, color = Color(0xFF1B5E20))
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { newValue ->
                        onSunnahStateChange(updateState(newValue))
                    },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50))
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
            "Hari ini aku bersyukur karena...",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20)
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = syukur,
            onValueChange = onSyukurChange,
            placeholder = {
                Text(
                    "Tuliskan rasa syukurmu hari ini...",
                    color = Color(0xFFB0BEC5)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4CAF50),
                unfocusedBorderColor = Color(0xFFB0BEC5),
                cursorColor = Color(0xFF1B5E20),
                focusedLabelColor = Color(0xFF4CAF50)
            )
        )
    }
}