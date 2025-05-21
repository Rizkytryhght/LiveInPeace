package com.example.liveinpeace.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.liveinpeace.R
import com.example.liveinpeace.data.local.room.AppDatabase
import com.example.liveinpeace.data.local.room.Mood
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var moodLineChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("DashboardActivity", "onCreate called")
        setContentView(R.layout.activity_dashboard)

        // Check layout
        val moodLayout = findViewById<LinearLayout>(R.id.moodSelectionLayout)
        Log.d("DashboardActivity", "moodSelectionLayout: $moodLayout")

        // Initialize Room Database
        try {
            db = AppDatabase.getDatabase(this)
            Log.d("DashboardActivity", "Database initialized")
        } catch (e: Exception) {
            Log.e("DashboardActivity", "Error initializing database: ${e.message}")
        }

        // Reference UI elements
        val moodHappy = findViewById<ImageView>(R.id.moodHappy)
        val moodSad = findViewById<ImageView>(R.id.moodSad)
        val moodAnxious = findViewById<ImageView>(R.id.moodAnxious)
        val moodAngry = findViewById<ImageView>(R.id.moodAngry)
        val moodCalm = findViewById<ImageView>(R.id.moodCalm)
        moodLineChart = findViewById(R.id.moodLineChart)

        // Log to check if views are found
        Log.d("DashboardActivity", "moodHappy: $moodHappy")
        Log.d("DashboardActivity", "moodSad: $moodSad")
        Log.d("DashboardActivity", "moodAnxious: $moodAnxious")
        Log.d("DashboardActivity", "moodAngry: $moodAngry")
        Log.d("DashboardActivity", "moodCalm: $moodCalm")
        Log.d("DashboardActivity", "moodLineChart: $moodLineChart")

        // Set click listeners
        moodHappy.setOnClickListener {
            Log.d("DashboardActivity", "Happy icon clicked")
            saveMood("Senang")
            updateMoodSelection(moodHappy, moodSad, moodAnxious, moodAngry, moodCalm)
            updateMoodChart()
        }
        moodSad.setOnClickListener {
            Log.d("DashboardActivity", "Sad icon clicked")
            saveMood("Sad")
            updateMoodSelection(moodSad, moodHappy, moodAnxious, moodAngry, moodCalm)
            updateMoodChart()
        }
        moodAnxious.setOnClickListener {
            Log.d("DashboardActivity", "Anxious icon clicked")
            saveMood("Anxious")
            updateMoodSelection(moodAnxious, moodHappy, moodSad, moodAngry, moodCalm)
            updateMoodChart()
        }
        moodAngry.setOnClickListener {
            Log.d("DashboardActivity", "Angry icon clicked")
            saveMood("Angry")
            updateMoodSelection(moodAngry, moodHappy, moodSad, moodAnxious, moodCalm)
            updateMoodChart()
        }
        moodCalm.setOnClickListener {
            Log.d("DashboardActivity", "Calm icon clicked")
            saveMood("Calm")
            updateMoodSelection(moodCalm, moodHappy, moodSad, moodAnxious, moodAngry)
            updateMoodChart()
        }

        // Setup chart
        setupChart()
        updateMoodChart()
    }

    private fun saveMood(moodDescription: String) {
        Log.d("DashboardActivity", "Saving mood: $moodDescription")
        val mood = Mood(
            userId = "user123",
            moodDescription = moodDescription,
            timestamp = System.currentTimeMillis()
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.moodDao().insert(mood)
                Log.d("DashboardActivity", "Mood inserted: $mood")
                withContext(Dispatchers.Main) {
                    Log.d("DashboardActivity", "Showing toast for $moodDescription")
                    Toast.makeText(applicationContext, "Mood $moodDescription disimpan!", Toast.LENGTH_LONG).show()
                    checkDatabase()
                }
            } catch (e: Exception) {
                Log.e("DashboardActivity", "Error saving mood: ${e.message}")
            }
        }
    }

    private fun checkDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val moods = db.moodDao().getMoodsByUser("user123")
                withContext(Dispatchers.Main) {
                    Log.d("DashboardActivity", "Moods in DB: $moods")
                }
            } catch (e: Exception) {
                Log.e("DashboardActivity", "Error reading database: ${e.message}")
            }
        }
    }

    private fun updateMoodSelection(selected: ImageView, vararg others: ImageView) {
        Log.d("DashboardActivity", "Selected ImageView: ${selected.id}")
        selected.isSelected = true
        others.forEach { it.isSelected = false }
    }

    private fun setupChart() {
        moodLineChart.description.isEnabled = false
        moodLineChart.setTouchEnabled(true)
        moodLineChart.isDragEnabled = true
        moodLineChart.setScaleEnabled(true)
        moodLineChart.setPinchZoom(true)
        moodLineChart.xAxis.setDrawGridLines(false)
        moodLineChart.axisLeft.setDrawGridLines(false)
        moodLineChart.axisRight.isEnabled = false
        Log.d("DashboardActivity", "Chart initialized")
    }

    private fun updateMoodChart() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val moods = db.moodDao().getMoodsByUser("user123")
                val entries = moods.mapIndexed { index, mood ->
                    val moodValue = when (mood.moodDescription) {
                        "Senang" -> 5f
                        "Calm" -> 4f
                        "Anxious" -> 3f
                        "Sad" -> 2f
                        "Angry" -> 1f
                        else -> 0f
                    }
                    Entry(index.toFloat(), moodValue)
                }
                withContext(Dispatchers.Main) {
                    val dataSet = LineDataSet(entries, "Mood History")
                    dataSet.color = 0xFF6200EE.toInt()
                    dataSet.setCircleColor(0xFF6200EE.toInt())
                    dataSet.lineWidth = 2f
                    dataSet.circleRadius = 4f
                    dataSet.setDrawValues(false)
                    moodLineChart.data = LineData(dataSet)
                    moodLineChart.invalidate()
                    Log.d("DashboardActivity", "Chart updated with ${entries.size} entries")
                }
            } catch (e: Exception) {
                Log.e("DashboardActivity", "Error updating chart: ${e.message}")
            }
        }
    }
}

//import android.os.Bundle
//import android.util.Log
//import android.widget.ImageView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.liveinpeace.R
//import com.example.liveinpeace.data.local.room.AppDatabase
//import com.example.liveinpeace.data.local.room.Mood
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class DashboardActivity : AppCompatActivity() {
//    private lateinit var db: AppDatabase
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Log.d("DashboardActivity", "onCreate called")
//        setContentView(R.layout.activity_dashboard)
//
//        // Initialize Room Database
//        db = AppDatabase.getDatabase(this)
//        Log.d("DashboardActivity", "Database initialized")
//
//        // Reference UI elements
//        val moodHappy = findViewById<ImageView>(R.id.moodHappy)
//        val moodSad = findViewById<ImageView>(R.id.moodSad)
//        val moodAnxious = findViewById<ImageView>(R.id.moodAnxious)
//        val moodAngry = findViewById<ImageView>(R.id.moodAngry)
//        val moodCalm = findViewById<ImageView>(R.id.moodCalm)
//
//        // Log to check if views are found
//        Log.d("DashboardActivity", "moodHappy: $moodHappy")
//        Log.d("DashboardActivity", "moodSad: $moodSad")
//        Log.d("DashboardActivity", "moodAnxious: $moodAnxious")
//        Log.d("DashboardActivity", "moodAngry: $moodAngry")
//        Log.d("DashboardActivity", "moodCalm: $moodCalm")
//
//        // Set listeners to save mood on click
//        moodHappy.setOnClickListener {
//            Log.d("DashboardActivity", "Happy icon clicked")
//            saveMood("Senang")
//            updateMoodSelection(moodHappy, moodSad, moodAnxious, moodAngry, moodCalm)
//        }
//        moodSad.setOnClickListener {
//            Log.d("DashboardActivity", "Sad icon clicked")
//            saveMood("Sad")
//            updateMoodSelection(moodSad, moodHappy, moodAnxious, moodAngry, moodCalm)
//        }
//        moodAnxious.setOnClickListener {
//            Log.d("DashboardActivity", "Anxious icon clicked")
//            saveMood("Anxious")
//            updateMoodSelection(moodAnxious, moodHappy, moodSad, moodAngry, moodCalm)
//        }
//        moodAngry.setOnClickListener {
//            Log.d("DashboardActivity", "Angry icon clicked")
//            saveMood("Angry")
//            updateMoodSelection(moodAngry, moodHappy, moodSad, moodAnxious, moodCalm)
//        }
//        moodCalm.setOnClickListener {
//            Log.d("DashboardActivity", "Calm icon clicked")
//            saveMood("Calm")
//            updateMoodSelection(moodCalm, moodHappy, moodSad, moodAnxious, moodAngry)
//        }
//    }
//
//    private fun saveMood(moodDescription: String) {
//        Log.d("DashboardActivity", "Saving mood: $moodDescription")
//        val mood = Mood(
//            userId = "user123",
//            moodDescription = moodDescription,
//            timestamp = System.currentTimeMillis()
//        )
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                db.moodDao().insert(mood)
//                Log.d("DashboardActivity", "Mood inserted: $mood")
//                withContext(Dispatchers.Main) {
//                    Log.d("DashboardActivity", "Showing toast for $moodDescription")
//                    Toast.makeText(applicationContext, "Mood $moodDescription disimpan!", Toast.LENGTH_LONG).show()
//                    checkDatabase()
//                }
//            } catch (e: Exception) {
//                Log.e("DashboardActivity", "Error saving mood: ${e.message}")
//            }
//        }
//    }
//
//    private fun checkDatabase() {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val moods = db.moodDao().getMoodsByUser("user123")
//                withContext(Dispatchers.Main) {
//                    Log.d("DashboardActivity", "Moods in DB: $moods")
//                }
//            } catch (e: Exception) {
//                Log.e("DashboardActivity", "Error reading database: ${e.message}")
//            }
//        }
//    }
//
//    private fun updateMoodSelection(selected: ImageView, vararg others: ImageView) {
//        Log.d("DashboardActivity", "Selected ImageView: ${selected.id}")
//        selected.isSelected = true
//        others.forEach { it.isSelected = false }
//    }
//}

//import android.os.Bundle
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.liveinpeace.R
//import com.example.liveinpeace.data.local.room.AppDatabase
//import com.example.liveinpeace.data.local.room.Mood
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
//class DashboardActivity : AppCompatActivity() {
//    private lateinit var db: AppDatabase
//    private var selectedMood: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_dashboard)
//
//        // Inisialisasi Room Database
//        db = AppDatabase.getDatabase(this)
//
//        // Referensi elemen UI dari XML
//        val moodHappy = findViewById<ImageView>(R.id.moodHappy)
//        val moodSad = findViewById<ImageView>(R.id.moodSad)
//        val moodAnxious = findViewById<ImageView>(R.id.moodAnxious)
//        val moodAngry = findViewById<ImageView>(R.id.moodAngry)
//        val moodCalm = findViewById<ImageView>(R.id.moodCalm)
//        val saveMoodButton = findViewById<Button>(R.id.saveMoodButton)
//
//        // Set listener untuk pilih mood
//        moodHappy.setOnClickListener {
//            selectedMood = "Happy"
//            updateMoodSelection(moodHappy, moodSad, moodAnxious, moodAngry, moodCalm)
//        }
//
//        moodSad.setOnClickListener {
//            selectedMood = "Sad"
//            updateMoodSelection(moodSad, moodHappy, moodAnxious, moodAngry, moodCalm)
//        }
//
//        moodAnxious.setOnClickListener {
//            selectedMood = "Anxious"
//            updateMoodSelection(moodAnxious, moodHappy, moodSad, moodAngry, moodCalm)
//        }
//
//        moodAngry.setOnClickListener {
//            selectedMood = "Angry"
//            updateMoodSelection(moodAngry, moodHappy, moodSad, moodAnxious, moodCalm)
//        }
//
//        moodCalm.setOnClickListener {
//            selectedMood = "Calm"
//            updateMoodSelection(moodCalm, moodHappy, moodSad, moodAnxious, moodAngry)
//        }
//
//        // Simpan data ke Room DB saat tombol diklik
//        saveMoodButton.setOnClickListener {
//            if (selectedMood != null) {
//                val mood = Mood(
//                    mood = selectedMood!!,
//                    timestamp = System.currentTimeMillis(),
//                    userId = "user123" // Ganti dengan ID user dinamis kalau ada
//                )
//
//                CoroutineScope(Dispatchers.IO).launch {
//                    db.moodDao().insert(mood)
//                    runOnUiThread {
//                        Toast.makeText(this@DashboardActivity, "Mood saved successfully!", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            } else {
//                Toast.makeText(this, "Please select a mood!", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun updateMoodSelection(selected: ImageView, vararg others: ImageView) {
//        selected.isSelected = true
//        others.forEach { it.isSelected = false }
//    }
//}

//import android.os.Bundle
//import android.view.View
//import android.widget.*
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import com.example.liveinpeace.R
//import com.example.liveinpeace.viewModel.MoodViewModel
//import com.github.mikephil.charting.charts.LineChart
//import com.github.mikephil.charting.components.XAxis
//import com.github.mikephil.charting.data.Entry
//import com.github.mikephil.charting.data.LineData
//import com.github.mikephil.charting.data.LineDataSet
//import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
//import java.text.SimpleDateFormat
//import java.util.*
//import androidx.lifecycle.lifecycleScope
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch
//
//class DashboardActivity : AppCompatActivity() {
//
//    private val moodViewModel: MoodViewModel by viewModels()
//    private var selectedMood: String? = null
//    private var selectedMoodImage: ImageView? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_dashboard)
//
//        val moodHappy = findViewById<ImageView>(R.id.moodHappy)
//        val moodSad = findViewById<ImageView>(R.id.moodSad)
//        val moodAnxious = findViewById<ImageView>(R.id.moodAnxious)
//        val saveMoodButton = findViewById<Button>(R.id.saveMoodButton)
//        val viewSpinner = findViewById<Spinner>(R.id.viewSpinner)
//        val moodLineChart = findViewById<LineChart>(R.id.moodLineChart)
//
//        // Set click listeners for mood images
//        moodHappy.setOnClickListener {
//            selectMood("Senang", moodHappy)
//        }
//        moodSad.setOnClickListener {
//            selectMood("Sedih", moodSad)
//        }
//        moodAnxious.setOnClickListener {
//            selectMood("Cemas", moodAnxious)
//        }
//
//        lifecycleScope.launch {
//            moodViewModel.moods.collectLatest { moodList ->
//                val today = getTodayDate()
//                val alreadyFilled = moodList.any {
//                    getDateFromTimestamp(it.timestamp) == today
//                }
//
//                if (alreadyFilled) {
//                    moodHappy.isEnabled = false
//                    moodSad.isEnabled = false
//                    moodAnxious.isEnabled = false
//                    saveMoodButton.isEnabled = false
//                    Toast.makeText(this@DashboardActivity, "Mood hari ini sudah diisi", Toast.LENGTH_SHORT).show()
//                }
//
//                // Update chart based on selected view
//                val selectedView = viewSpinner.selectedItem.toString()
//                val moodScores = mapOf(
//                    "Senang" to 4f, "Sedih" to 1f, "Cemas" to 2f
//                )
//                val filteredMoods = when (selectedView) {
//                    "Day" -> {
//                        val today = getTodayDate()
//                        moodList.filter { getDateFromTimestamp(it.timestamp) == today }
//                    }
//                    "Week" -> {
//                        val oneWeekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000
//                        moodList.filter { it.timestamp >= oneWeekAgo }
//                    }
//                    else -> moodList
//                }.sortedBy { it.timestamp }
//
//                val entries = filteredMoods.mapIndexed { index, moodEntry ->
//                    Entry(index.toFloat(), moodScores[moodEntry.mood] ?: 0f)
//                }
//                val labels = filteredMoods.map { moodEntry ->
//                    SimpleDateFormat("EEE", Locale.getDefault()).format(Date(moodEntry.timestamp))
//                }
//
//                val dataSet = LineDataSet(entries, "Mood Trend").apply {
//                    color = getColor(R.color.primary_green)
//                    setDrawCircles(true)
//                    setCircleColor(getColor(R.color.primary_green))
//                    setDrawValues(false)
//                    lineWidth = 2f
//                }
//                val lineData = LineData(dataSet)
//                moodLineChart.data = lineData
//                moodLineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
//                moodLineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
//                moodLineChart.xAxis.setDrawGridLines(false)
//                moodLineChart.axisLeft.setDrawGridLines(false)
//                moodLineChart.axisRight.isEnabled = false
//                moodLineChart.description.isEnabled = false
//                moodLineChart.invalidate()
//            }
//        }
//
//        // Update chart when spinner selection changes
//        viewSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                lifecycleScope.launch {
//                    moodViewModel.moods.collectLatest { moodList ->
//                        val selectedView = parent.getItemAtPosition(position).toString()
//                        val moodScores = mapOf(
//                            "Senang" to 4f, "Sedih" to 1f, "Cemas" to 2f
//                        )
//                        val filteredMoods = when (selectedView) {
//                            "Day" -> {
//                                val today = getTodayDate()
//                                moodList.filter { getDateFromTimestamp(it.timestamp) == today }
//                            }
//                            "Week" -> {
//                                val oneWeekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000
//                                moodList.filter { it.timestamp >= oneWeekAgo }
//                            }
//                            else -> moodList
//                        }.sortedBy { it.timestamp }
//
//                        val entries = filteredMoods.mapIndexed { index, moodEntry ->
//                            Entry(index.toFloat(), moodScores[moodEntry.mood] ?: 0f)
//                        }
//                        val labels = filteredMoods.map { moodEntry ->
//                            SimpleDateFormat("EEE", Locale.getDefault()).format(Date(moodEntry.timestamp))
//                        }
//
//                        val dataSet = LineDataSet(entries, "Mood Trend").apply {
//                            color = getColor(R.color.primary_green)
//                            setDrawCircles(true)
//                            setCircleColor(getColor(R.color.primary_green))
//                            setDrawValues(false)
//                            lineWidth = 2f
//                        }
//                        val lineData = LineData(dataSet)
//                        moodLineChart.data = lineData
//                        moodLineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
//                        moodLineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
//                        moodLineChart.xAxis.setDrawGridLines(false)
//                        moodLineChart.axisLeft.setDrawGridLines(false)
//                        moodLineChart.axisRight.isEnabled = false
//                        moodLineChart.description.isEnabled = false
//                        moodLineChart.invalidate()
//                    }
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {}
//        }
//
//        saveMoodButton.setOnClickListener {
//            if (selectedMood != null) {
//                moodViewModel.saveMood(selectedMood!!)
//                Toast.makeText(this, "Mood disimpan!", Toast.LENGTH_SHORT).show()
//                // Reset selection
//                selectedMoodImage?.isSelected = false
//                selectedMood = null
//                selectedMoodImage = null
//            } else {
//                Toast.makeText(this, "Pilih mood dulu ya", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        moodViewModel.loadMoods()
//    }
//
//    private fun selectMood(mood: String, imageView: ImageView) {
//        selectedMoodImage?.isSelected = false
//        selectedMood = mood
//        selectedMoodImage = imageView
//        imageView.isSelected = true
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

//import android.os.Bundle
//import android.widget.Button
//import android.widget.RadioButton
//import android.widget.RadioGroup
//import android.widget.Toast
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import com.example.liveinpeace.R
//import com.example.liveinpeace.viewModel.MoodViewModel
//import java.text.SimpleDateFormat
//import java.util.*
//import androidx.lifecycle.lifecycleScope
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch
//import com.github.mikephil.charting.charts.BarChart
//import com.github.mikephil.charting.components.XAxis
//import com.github.mikephil.charting.data.BarData
//import com.github.mikephil.charting.data.BarDataSet
//import com.github.mikephil.charting.data.BarEntry
//import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
//
//class DashboardActivity : AppCompatActivity() {
//
//    private val moodViewModel: MoodViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_dashboard)
//
//        val moodRadioGroup = findViewById<RadioGroup>(R.id.moodRadioGroup)
//        val saveMoodButton = findViewById<Button>(R.id.saveMoodButton)
//        val moodBarChart = findViewById<BarChart>(R.id.moodBarChart)
//
//        lifecycleScope.launch {
//            moodViewModel.moods.collectLatest { moodList ->
//                val today = getTodayDate()
//                val alreadyFilled = moodList.any {
//                    getDateFromTimestamp(it.timestamp) == today
//                }
//
//                if (alreadyFilled) {
//                    moodRadioGroup.isEnabled = false
//                    for (i in 0 until moodRadioGroup.childCount) {
//                        moodRadioGroup.getChildAt(i).isEnabled = false
//                    }
//                    saveMoodButton.isEnabled = false
//                    Toast.makeText(this@DashboardActivity, "Mood hari ini sudah diisi", Toast.LENGTH_SHORT).show()
//                }
//
//                // ✨ Bikin grafik batang mood di sini
//                val moodCounts = moodList.groupingBy { it.mood }.eachCount()
//
//                val entries = moodCounts.entries.mapIndexed { index, entry ->
//                    BarEntry(index.toFloat(), entry.value.toFloat())
//                }
//
//                val dataSet = BarDataSet(entries, "Statistik Mood")
//                dataSet.color = getColor(R.color.primary_green) // ganti sesuai warna hijau kamu
//
//                val barData = BarData(dataSet)
//                barData.barWidth = 0.9f
//
//                moodBarChart.data = barData
//
//                val labels = moodCounts.keys.toList()
//                moodBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
//                moodBarChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
//                moodBarChart.xAxis.setDrawGridLines(false)
//                moodBarChart.axisLeft.setDrawGridLines(false)
//                moodBarChart.axisRight.isEnabled = false
//                moodBarChart.description.isEnabled = false
//                moodBarChart.setFitBars(true)
//                moodBarChart.invalidate()
//            }
//        }
//
//        saveMoodButton.setOnClickListener {
//            val selectedId = moodRadioGroup.checkedRadioButtonId
//            if (selectedId != -1) {
//                val selectedMood =
//                    findViewById<RadioButton>(selectedId).text.toString()
//                moodViewModel.saveMood(selectedMood)
//                Toast.makeText(this, "Mood disimpan!", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "Pilih mood dulu ya", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        moodViewModel.loadMoods()
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

//import android.os.Bundle
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.liveinpeace.viewModel.MoodViewModel
//import com.github.mikephil.charting.charts.LineChart
//import com.github.mikephil.charting.components.XAxis
//import com.github.mikephil.charting.data.Entry
//import com.github.mikephil.charting.data.LineData
//import com.github.mikephil.charting.data.LineDataSet
//import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
//import kotlinx.coroutines.launch
//import java.text.SimpleDateFormat
//import java.util.*
//
//class DashboardActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            DashboardScreen()
//        }
//    }
//}
//
//@Composable
//fun DashboardScreen() {
//    val moodViewModel: MoodViewModel = viewModel()
//    val context = LocalContext.current
//    var selectedMood by remember { mutableStateOf<String?>(null) }
//    val moods by moodViewModel.moods.collectAsState(initial = emptyList())
//    var selectedView by remember { mutableStateOf("Week") }
//    var dropdownExpanded by remember { mutableStateOf(false) }
//    val scope = rememberCoroutineScope()
//
//    val today = remember { getTodayDate() }
//    val alreadyFilled = remember(moods) { moods.any { getDateFromTimestamp(it.timestamp) == today } }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(text = "Gimana perasaanmu, sobat?", style = MaterialTheme.typography.h6)
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Mood Selection
//        Row {
//            listOf("Angry", "Sad", "Neutral", "Happy", "Very Happy").forEach { mood ->
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    RadioButton(
//                        selected = selectedMood == mood,
//                        onClick = { selectedMood = mood },
//                        enabled = !alreadyFilled
//                    )
//                    Text(text = mood)
//                }
//                Spacer(modifier = Modifier.width(8.dp))
//            }
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Save Mood Button
//        Button(
//            onClick = {
//                if (alreadyFilled) {
//                    Toast.makeText(context, "Mood hari ini sudah diisi", Toast.LENGTH_SHORT).show()
//                } else if (selectedMood == null) {
//                    Toast.makeText(context, "Pilih mood dulu ya", Toast.LENGTH_SHORT).show()
//                } else {
//                    scope.launch {
//                        selectedMood?.let { mood ->
//                            moodViewModel.saveMood(mood)
//                            selectedMood = null
//                        }
//                    }
//                    Toast.makeText(context, "Mood disimpan!", Toast.LENGTH_SHORT).show()
//                }
//            },
//            enabled = !alreadyFilled
//        ) {
//            Text("Save Mood")
//        }
//        Spacer(modifier = Modifier.height(32.dp))
//
//        // View Toggle Dropdown
//        Box {
//            Button(onClick = { dropdownExpanded = true }) {
//                Text(selectedView)
//            }
//            DropdownMenu(
//                expanded = dropdownExpanded,
//                onDismissRequest = { dropdownExpanded = false }
//            ) {
//                DropdownMenuItem(onClick = {
//                    selectedView = "Day"
//                    dropdownExpanded = false
//                }) { Text("Day") }
//                DropdownMenuItem(onClick = {
//                    selectedView = "Week"
//                    dropdownExpanded = false
//                }) { Text("Week") }
//            }
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Mood Trend Line Chart
//        AndroidView(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(200.dp),
//            factory = { context ->
//                LineChart(context).apply {
//                    description.isEnabled = false
//                    axisRight.isEnabled = false
//                    xAxis.position = XAxis.XAxisPosition.BOTTOM
//                    xAxis.setDrawGridLines(false)
//                    axisLeft.setDrawGridLines(false)
//                }
//            },
//            update = { lineChart ->
//                val moodScores = mapOf(
//                    "Angry" to 0f, "Sad" to 1f, "Neutral" to 2f, "Happy" to 3f, "Very Happy" to 4f
//                )
//                val filteredMoods = when (selectedView) {
//                    "Day" -> {
//                        val today = getTodayDate()
//                        moods.filter { getDateFromTimestamp(it.timestamp) == today }
//                    }
//                    "Week" -> {
//                        val oneWeekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000
//                        moods.filter { it.timestamp >= oneWeekAgo }
//                    }
//                    else -> moods
//                }.sortedBy { it.timestamp }
//
//                val entries: List<Entry> = filteredMoods.mapIndexed { index, moodEntry ->
//                    Entry(index.toFloat(), moodScores[moodEntry.mood] ?: 0f)
//                }
//                val labels = filteredMoods.map { moodEntry ->
//                    SimpleDateFormat("EEE", Locale.getDefault()).format(Date(moodEntry.timestamp))
//                }
//
//                val dataSet = LineDataSet(entries, "Mood Trend").apply {
//                    color = android.graphics.Color.GREEN
//                    setDrawCircles(true)
//                    setCircleColor(android.graphics.Color.GREEN)
//                    setDrawValues(false)
//                    lineWidth = 2f
//                }
//                val lineData = LineData(dataSet)
//                lineChart.data = lineData
//                lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
//                lineChart.axisLeft.axisMinimum = 0f
//                lineChart.axisLeft.axisMaximum = 4f
//                lineChart.axisLeft.labelCount = 5
//                lineChart.axisLeft.valueFormatter = IndexAxisValueFormatter(
//                    arrayOf("Angry", "Sad", "Neutral", "Happy", "Very Happy")
//                )
//                lineChart.invalidate()
//            }
//        )
//    }
//
//    LaunchedEffect(Unit) {
//        moodViewModel.loadMoods()
//    }
//}
//
//private fun getTodayDate(): String {
//    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//    return sdf.format(Date())
//}
//
//private fun getDateFromTimestamp(timestamp: Long): String {
//    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//    return sdf.format(Date(timestamp))
//}