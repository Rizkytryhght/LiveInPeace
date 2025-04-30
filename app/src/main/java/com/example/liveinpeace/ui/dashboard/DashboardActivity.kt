package com.example.liveinpeace.ui.dashboard

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.liveinpeace.R
import com.example.liveinpeace.viewModel.MoodViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    private val moodViewModel: MoodViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val moodRadioGroup = findViewById<RadioGroup>(R.id.moodRadioGroup)
        val saveMoodButton = findViewById<Button>(R.id.saveMoodButton)

        // Cek apakah user sudah input mood hari ini
        lifecycleScope.launch {
            moodViewModel.moods.collectLatest { moodList ->
                val today = getTodayDate()
                val alreadyFilled = moodList.any {
                    getDateFromTimestamp(it.timestamp) == today
                }
                if (alreadyFilled) {
                    moodRadioGroup.isEnabled = false
                    for (i in 0 until moodRadioGroup.childCount) {
                        moodRadioGroup.getChildAt(i).isEnabled = false
                    }
                    saveMoodButton.isEnabled = false
                    Toast.makeText(this@DashboardActivity, "Mood hari ini sudah diisi", Toast.LENGTH_SHORT).show()
                }
            }
        }

        saveMoodButton.setOnClickListener {
            val selectedId = moodRadioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedMood =
                    findViewById<RadioButton>(selectedId).text.toString()
                moodViewModel.saveMood(selectedMood)
                Toast.makeText(this, "Mood disimpan!", Toast.LENGTH_SHORT).show()
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