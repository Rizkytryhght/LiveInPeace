package com.example.liveinpeace.ui.reminder

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.liveinpeace.ui.receiver.ReminderReceiver
import com.example.liveinpeace.viewModel.ReminderViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

class ReminderActivity : ComponentActivity() {

    companion object {
        const val REQUEST_NOTIFICATION_PERMISSION = 1001
        private const val TAG = "ReminderActivity"

        // Prayer types for intent extras
        const val EXTRA_PRAYER_TYPE = "prayer_type"
        const val PRAYER_FAJR = "fajr"
        const val PRAYER_DHUHR = "dhuhr"
        const val PRAYER_ASR = "asr"
        const val PRAYER_MAGHRIB = "maghrib"
        const val PRAYER_ISHA = "isha"
        const val DZIKIR_PAGI = "dzikir_pagi"
        const val DZIKIR_PETANG = "dzikir_petang"
    }

    private val viewModel: ReminderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up permissions
        checkAndRequestPermissions()

        // Set content with Compose
        setContent {
            ReminderScreen()
        }

        // Observe viewModel data and set alarms when times are available
        setupReminderObservers()
    }

    private fun setupReminderObservers() {
        // Prayer time observers
        observePrayerTime(viewModel.fajrTime, PRAYER_FAJR, "Waktu Sholat Subuh 5 menit lagi")
        observePrayerTime(viewModel.dhuhrTime, PRAYER_DHUHR, "Waktu Sholat Dzuhur 5 menit lagi")
        observePrayerTime(viewModel.asrTime, PRAYER_ASR, "Waktu Sholat Ashar 5 menit lagi")
        observePrayerTime(viewModel.maghribTime, PRAYER_MAGHRIB, "Waktu Sholat Maghrib 5 menit lagi")
        observePrayerTime(viewModel.ishaTime, PRAYER_ISHA, "Waktu Sholat Isya 5 menit lagi")

        // Legacy dzikir observers (can be kept for backward compatibility)
        observePrayerTime(viewModel.dzikirPagiTime, DZIKIR_PAGI, "Waktunya Dzikir Pagi!")
        observePrayerTime(viewModel.dzikirPetangTime, DZIKIR_PETANG, "Waktunya Dzikir Petang!")
    }

    private fun observePrayerTime(timeFlow: kotlinx.coroutines.flow.StateFlow<String>, prayerType: String, message: String) {
        lifecycleScope.launch {
            timeFlow.collectLatest { timeString ->
                if (timeString != "..." && timeString != "Error" && timeString != "Failed" && timeString != "N/A") {
                    // Create calendar for 5 minutes before prayer time
                    val calendar = if (prayerType == DZIKIR_PAGI || prayerType == DZIKIR_PETANG) {
                        // For dzikir, keep the original time
                        convertToCalendar(timeString)
                    } else {
                        // For prayers, set reminder 5 minutes before
                        getTimeBeforePrayer(timeString, 5)
                    }

                    setReminder(calendar, message, prayerType)
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Check & request notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            }
        }

        // Request schedule exact alarm permission (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
                Toast.makeText(this, "Mohon aktifkan izin Exact Alarm di pengaturan.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun convertToCalendar(time: String): Calendar {
        val cleanedTime = time.split(" ")[0] // Remove timezone if present
        val timeParts = cleanedTime.split(":")
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
        calendar.set(Calendar.MINUTE, timeParts[1].toInt())
        calendar.set(Calendar.SECOND, 0)

        // If time has already passed today, set for tomorrow
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return calendar
    }

    private fun getTimeBeforePrayer(time: String, minutesBefore: Int): Calendar {
        val calendar = convertToCalendar(time)
        calendar.add(Calendar.MINUTE, -minutesBefore)
        return calendar
    }

    private fun setReminder(calendar: Calendar, message: String, prayerType: String) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra("reminder_message", message)
            putExtra(EXTRA_PRAYER_TYPE, prayerType)
        }

        // Use prayerType to create unique request code for each prayer
        val requestCode = prayerType.hashCode()

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                    Log.d(TAG, "Exact alarm set for $prayerType: $message at ${calendar.time}")
                } else {
                    Toast.makeText(this, "Tidak punya izin untuk schedule exact alarm.", Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "Schedule exact alarm ditolak.")
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
                Log.d(TAG, "Alarm set for $prayerType: $message at ${calendar.time}")
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Tidak bisa set alarm karena tidak ada permission.", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "SecurityException: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting alarm: ${e.message}")
        }
    }

    // Handle notification permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Notifikasi diizinkan")
            } else {
                Toast.makeText(this, "Izin notifikasi ditolak.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}