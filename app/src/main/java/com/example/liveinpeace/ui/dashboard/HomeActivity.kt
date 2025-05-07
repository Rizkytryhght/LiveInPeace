package com.example.liveinpeace.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.liveinpeace.R
import com.example.liveinpeace.viewModel.MoodViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.lifecycleScope
import com.example.liveinpeace.ui.features.FeatureListActivity
import com.example.liveinpeace.ui.note.NoteActivity
import com.example.liveinpeace.ui.profile.ProfileActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private val moodViewModel: MoodViewModel by viewModels()
    private var selectedMood: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val moodHappyButton = findViewById<Button>(R.id.moodHappyButton)
        val moodSadButton = findViewById<Button>(R.id.moodSadButton)
        val moodAnxiousButton = findViewById<Button>(R.id.moodAnxiousButton)
        val saveMoodButton = findViewById<Button>(R.id.saveMoodButton)
        val moodBarChart = findViewById<BarChart>(R.id.moodBarChart)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Atur item yang sedang aktif
        bottomNavigationView.selectedItemId = R.id.nav_home

        // Navigasi antar halaman
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true // sudah di halaman ini
                R.id.nav_notes -> {
                    startActivity(Intent(this, NoteActivity::class.java))
                    true
                }
                R.id.nav_features -> {
                    startActivity(Intent(this, FeatureListActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Observasi data mood
        lifecycleScope.launch {
            moodViewModel.moods.collectLatest { moodList ->
                val today = getTodayDate()
                val alreadyFilled = moodList.any {
                    getDateFromTimestamp(it.timestamp) == today
                }

                if (alreadyFilled) {
                    moodHappyButton.isEnabled = false
                    moodSadButton.isEnabled = false
                    moodAnxiousButton.isEnabled = false
                    saveMoodButton.isEnabled = false
                    Toast.makeText(this@HomeActivity, "Mood hari ini sudah diisi", Toast.LENGTH_SHORT).show()
                }

                val moodCounts = moodList.groupingBy { it.mood }.eachCount()
                val entries = moodCounts.entries.mapIndexed { index, entry ->
                    BarEntry(index.toFloat(), entry.value.toFloat())
                }

                val dataSet = BarDataSet(entries, "Statistik Mood")
                dataSet.color = getColor(R.color.primary_green)

                val barData = BarData(dataSet)
                barData.barWidth = 0.9f

                moodBarChart.data = barData

                val labels = moodCounts.keys.toList()
                moodBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                moodBarChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                moodBarChart.xAxis.setDrawGridLines(false)
                moodBarChart.axisLeft.setDrawGridLines(false)
                moodBarChart.axisRight.isEnabled = false
                moodBarChart.description.isEnabled = false
                moodBarChart.setFitBars(true)
                moodBarChart.invalidate()
            }
        }

        // Tangani klik tombol mood
        moodHappyButton.setOnClickListener {
            selectedMood = "Senang"
            moodHappyButton.isSelected = true
            moodSadButton.isSelected = false
            moodAnxiousButton.isSelected = false
        }

        moodSadButton.setOnClickListener {
            selectedMood = "Sedih"
            moodHappyButton.isSelected = false
            moodSadButton.isSelected = true
            moodAnxiousButton.isSelected = false
        }

        moodAnxiousButton.setOnClickListener {
            selectedMood = "Cemas"
            moodHappyButton.isSelected = false
            moodSadButton.isSelected = false
            moodAnxiousButton.isSelected = true
        }

        saveMoodButton.setOnClickListener {
            if (selectedMood != null) {
                moodViewModel.saveMood(selectedMood!!)
                Toast.makeText(this, "Mood disimpan!", Toast.LENGTH_SHORT).show()
                selectedMood = null
                moodHappyButton.isSelected = false
                moodSadButton.isSelected = false
                moodAnxiousButton.isSelected = false
            } else {
                Toast.makeText(this, "Pilih mood dulu ya", Toast.LENGTH_SHORT).show()
            }
        }

        moodViewModel.loadMoods()
    }

    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getDateFromTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}