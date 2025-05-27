//package com.example.liveinpeace.ui.dashboard
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Toast
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.selection.selectableGroup
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.platform.ComposeView
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.lifecycle.lifecycleScope
//import com.example.liveinpeace.R
//import com.example.liveinpeace.model.MoodEntry
//import com.example.liveinpeace.ui.features.FeaturesListActivity
//import com.example.liveinpeace.ui.note.NoteActivity
//import com.example.liveinpeace.ui.profile.ProfileActivity
//import com.example.liveinpeace.viewModel.MoodViewModel
//import com.github.mikephil.charting.charts.BarChart
//import com.github.mikephil.charting.components.XAxis
//import com.github.mikephil.charting.data.BarData
//import com.github.mikephil.charting.data.BarDataSet
//import com.github.mikephil.charting.data.BarEntry
//import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
//import com.google.android.material.bottomnavigation.BottomNavigationView
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch
//import java.text.SimpleDateFormat
//import java.util.*
//
//class HomeActivity : AppCompatActivity() {
//    private val moodViewModel: MoodViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_home)
//
//        val moodComposeView = findViewById<ComposeView>(R.id.moodComposeView)
//        val moodBarChart = findViewById<BarChart>(R.id.moodBarChart)
//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
//
//        // Set ComposeView untuk tombol mood
//        moodComposeView.setContent {
//            MoodTrackerCompose(viewModel = moodViewModel)
//        }
//
//        // Atur item yang sedang aktif
//        bottomNavigationView.selectedItemId = R.id.nav_home
//
//        // Navigasi antar halaman
//        bottomNavigationView.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.nav_home -> true
//                R.id.nav_notes -> {
//                    startActivity(Intent(this, NoteActivity::class.java))
//                    true
//                }
//                R.id.nav_features -> {
//                    startActivity(Intent(this, FeaturesListActivity::class.java))
//                    true
//                }
//                R.id.nav_profile -> {
//                    startActivity(Intent(this, ProfileActivity::class.java))
//                    true
//                }
//                else -> false
//            }
//        }
//
//        // Observasi data mood
//        lifecycleScope.launch {
//            moodViewModel.moods.collectLatest { moodList: List<MoodEntry> ->
//                val today = getTodayDate()
//                val alreadyFilled = moodList.any { entry: MoodEntry ->
//                    getDateFromTimestamp(entry.timestamp) == today
//                }
//
//                if (alreadyFilled) {
//                    Toast.makeText(this@HomeActivity, "Mood hari ini sudah diisi", Toast.LENGTH_SHORT).show()
//                }
//
//                val moodCounts = moodList.groupingBy { entry: MoodEntry -> entry.mood }.eachCount()
//                val entries = moodCounts.entries.mapIndexed { index: Int, entry: Map.Entry<String, Int> ->
//                    BarEntry(index.toFloat(), entry.value.toFloat())
//                }
//
//                val dataSet = BarDataSet(entries, "Statistik Mood")
//                dataSet.color = getColor(R.color.primary_green)
//
//                val barData = BarData(dataSet)
//                barData.barWidth = 0.9f
//
//                moodBarChart.data = barData
//
//                val labels = moodCounts.keys.toList()
//                moodBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels.toTypedArray())
//                moodBarChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
//                moodBarChart.xAxis.setDrawGridLines(false)
//                moodBarChart.axisLeft.setDrawGridLines(false)
//                moodBarChart.axisRight.isEnabled = false
//                moodBarChart.description.isEnabled = false
//                moodBarChart.setFitBars(true)
//                moodBarChart.invalidate()
//            }
//        }
//    }
//
//    private fun getTodayDate(): String {
//        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        return sdf.format(Date())
//    }
//
//    private fun getDateFromTimestamp(timestamp: Long): String {
//        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        return sdf.format(Date(timestamp))
//    }
//}
//
//@Composable
//fun MoodTrackerCompose(viewModel: MoodViewModel) {
//    val context = LocalContext.current
//    val moods by viewModel.moods.collectAsStateWithLifecycle(initialValue = emptyList())
//    var selectedMood by remember { mutableStateOf<String?>(null) }
//
//    fun getTodayDate(): String {
//        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        return sdf.format(Date())
//    }
//
//    fun getDateFromTimestamp(timestamp: Long): String {
//        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        return sdf.format(Date(timestamp))
//    }
//
//    val today = getTodayDate()
//    val alreadyFilled = moods.any { entry: MoodEntry -> getDateFromTimestamp(entry.timestamp) == today }
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color.White)
//            .padding(vertical = 16.dp, horizontal = 8.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .selectableGroup()
//                .padding(vertical = 8.dp),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            MoodButton(
//                text = "Senang 😊",
//                isSelected = selectedMood == "Senang",
//                isEnabled = !alreadyFilled,
//                color = Color(0xFF4CAF50),
//                onClick = { selectedMood = "Senang" },
//                modifier = Modifier.weight(1f)
//            )
//            MoodButton(
//                text = "Sedih 😢",
//                isSelected = selectedMood == "Sedih",
//                isEnabled = !alreadyFilled,
//                color = Color(0xFF9E9E9E),
//                onClick = { selectedMood = "Sedih" },
//                modifier = Modifier.weight(1f)
//            )
//            MoodButton(
//                text = "Cemas 😓",
//                isSelected = selectedMood == "Cemas",
//                isEnabled = !alreadyFilled,
//                color = Color(0xFF2196F3),
//                onClick = { selectedMood = "Cemas" },
//                modifier = Modifier.weight(1f)
//            )
//        }
//
//        Button(
//            onClick = {
//                if (selectedMood != null) {
//                    viewModel.saveMood(selectedMood!!)
//                    Toast.makeText(context, "Mood disimpan!", Toast.LENGTH_SHORT).show()
//                    selectedMood = null
//                } else {
//                    Toast.makeText(context, "Pilih mood dulu ya", Toast.LENGTH_SHORT).show()
//                }
//            },
//            enabled = !alreadyFilled,
//            modifier = Modifier.padding(top = 12.dp),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = Color(0xFF4CAF50),
//                contentColor = Color.White,
//                disabledContainerColor = Color.Gray,
//                disabledContentColor = Color.White
//            )
//        ) {
//            Text("Simpan Mood")
//        }
//    }
//}
//
//@Composable
//fun MoodButton(
//    text: String,
//    isSelected: Boolean,
//    isEnabled: Boolean,
//    color: Color,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier ) {
//    Button(
//        onClick = onClick,
//        enabled = isEnabled,
//        modifier = modifier.padding(horizontal = 4.dp),
//        colors = ButtonDefaults.buttonColors(
//            containerColor =
//                if (isSelected)
//                    color
//                else
//                    color.copy(alpha = 0.7f),
//            contentColor = Color.White,
//            disabledContainerColor = Color.Gray,
//            disabledContentColor = Color.White ),
//        contentPadding = PaddingValues(12.dp) ) {
//        Text(
//            text = text,
//            fontSize = 14.sp,
//            textAlign = TextAlign.Center )
//    }
//}