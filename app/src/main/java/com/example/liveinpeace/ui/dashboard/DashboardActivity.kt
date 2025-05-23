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