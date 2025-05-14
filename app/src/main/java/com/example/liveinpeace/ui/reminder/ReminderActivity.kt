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

//import android.Manifest
//import android.app.AlarmManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.os.Build
//import android.os.Bundle
//import android.provider.Settings
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.viewModels
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.lifecycleScope
//import com.example.liveinpeace.ui.receiver.ReminderReceiver
//import com.example.liveinpeace.viewModel.ReminderViewModel
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch
//import java.util.*
//
//class ReminderActivity : ComponentActivity() {
//
//    companion object {
//        const val REQUEST_NOTIFICATION_PERMISSION = 1001
//    }
//
//    private val viewModel: ReminderViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Set up permissions
//        checkAndRequestPermissions()
//
//        // Set content with Compose
//        setContent {
//            ReminderScreen(viewModel = viewModel)
//        }
//
//        // Observe viewModel data and set alarms when times are available
//        setupReminderObservers()
//    }
//
//    private fun setupReminderObservers() {
//        lifecycleScope.launch {
//            viewModel.dzikirPagiTime.collectLatest { timeString ->
//                if (timeString != "..." && timeString != "Error" && timeString != "Failed" && timeString != "N/A") {
//                    val calendar = convertToCalendar(timeString)
//                    setReminder(calendar, "Waktunya Dzikir Pagi!")
//                }
//            }
//        }
//
//        lifecycleScope.launch {
//            viewModel.dzikirPetangTime.collectLatest { timeString ->
//                if (timeString != "..." && timeString != "Error" && timeString != "Failed" && timeString != "N/A") {
//                    val calendar = convertToCalendar(timeString)
//                    setReminder(calendar, "Waktunya Dzikir Petang!")
//                }
//            }
//        }
//    }
//
//    private fun checkAndRequestPermissions() {
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//        // Check & request notification permission (Android 13+)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
//                    REQUEST_NOTIFICATION_PERMISSION
//                )
//            }
//        }
//
//        // Request schedule exact alarm permission (Android 12+)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            if (!alarmManager.canScheduleExactAlarms()) {
//                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
//                startActivity(intent)
//                Toast.makeText(this, "Mohon aktifkan izin Exact Alarm di pengaturan.", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//
//    private fun convertToCalendar(time: String): Calendar {
//        val cleanedTime = time.split(" ")[0] // Remove timezone if present
//        val timeParts = cleanedTime.split(":")
//        val calendar = Calendar.getInstance()
//        calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
//        calendar.set(Calendar.MINUTE, timeParts[1].toInt())
//        calendar.set(Calendar.SECOND, 0)
//
//        // If time has already passed today, set for tomorrow
//        if (calendar.before(Calendar.getInstance())) {
//            calendar.add(Calendar.DAY_OF_YEAR, 1)
//        }
//
//        return calendar
//    }
//
//    private fun setReminder(calendar: Calendar, message: String) {
//        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
//        val intent = Intent(this, ReminderReceiver::class.java).apply {
//            putExtra("reminder_message", message)
//        }
//
//        val pendingIntent = PendingIntent.getBroadcast(
//            this,
//            message.hashCode(),
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                if (alarmManager.canScheduleExactAlarms()) {
//                    alarmManager.setExactAndAllowWhileIdle(
//                        AlarmManager.RTC_WAKEUP,
//                        calendar.timeInMillis,
//                        pendingIntent
//                    )
//                    Log.d("ReminderActivity", "Exact alarm set for $message at ${calendar.time}")
//                } else {
//                    Toast.makeText(this, "Tidak punya izin untuk schedule exact alarm.", Toast.LENGTH_SHORT).show()
//                    Log.w("ReminderActivity", "Schedule exact alarm ditolak.")
//                }
//            } else {
//                alarmManager.setExactAndAllowWhileIdle(
//                    AlarmManager.RTC_WAKEUP,
//                    calendar.timeInMillis,
//                    pendingIntent
//                )
//                Log.d("ReminderActivity", "Alarm set for $message at ${calendar.time}")
//            }
//        } catch (e: SecurityException) {
//            Toast.makeText(this, "Tidak bisa set alarm karena tidak ada permission.", Toast.LENGTH_SHORT).show()
//            Log.e("ReminderActivity", "SecurityException: ${e.message}")
//        } catch (e: Exception) {
//            Log.e("ReminderActivity", "Error setting alarm: ${e.message}")
//        }
//    }
//
//    // Handle notification permission result
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.d("ReminderActivity", "Notifikasi diizinkan")
//            } else {
//                Toast.makeText(this, "Izin notifikasi ditolak.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}



//import android.app.AlarmManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.os.Build
//import android.os.Bundle
//import android.provider.Settings
//import android.util.Log
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.liveinpeace.R
//import com.example.liveinpeace.api.RetrofitInstance
//import com.example.liveinpeace.model.SalatTimesResponse
//import com.example.liveinpeace.ui.receiver.ReminderReceiver
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.util.*
//
//class ReminderActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//            if (!alarmManager.canScheduleExactAlarms()) {
//                // Prompt the user to enable exact alarm scheduling in settings
//                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
//                startActivity(intent)
//            }
//        }
//
//        val city = "Bandung"
//        val country = "Indonesia"
//
//        RetrofitInstance.api.getTimingsByCity(city, country)
//            .enqueue(object : Callback<SalatTimesResponse> {
//                override fun onResponse(call: Call<SalatTimesResponse>, response: Response<SalatTimesResponse>) {
//                    if (response.isSuccessful) {
//                        val timings = response.body()?.data?.timings
//                        if (timings != null) {
//                            Log.d("PRAYER_API", "Fajr: ${timings.Fajr}, Maghrib: ${timings.Maghrib}")
//
//                            val pagiTime = timings.Fajr // Dzikir Pagi based on Fajr time
//                            val petangTime = timings.Maghrib // Dzikir Petang based on Maghrib time
//
//                            pagiTime?.let {
//                                val time = convertToCalendar(it)
//                                setReminder(time, "Waktunya Dzikir Pagi!")
//                            }
//
//                            petangTime?.let {
//                                val time = convertToCalendar(it)
//                                setReminder(time, "Waktunya Dzikir Petang!")
//                            }
//                        } else {
//                            Log.e("PRAYER_API", "Timings not found in the response!")
//                        }
//                    } else {
//                        Log.e("PRAYER_API", "Response not successful: ${response.code()}")
//                    }
//                }
//
//                override fun onFailure(call: Call<SalatTimesResponse>, t: Throwable) {
//                    Toast.makeText(this@ReminderActivity, "Failed to fetch salat times", Toast.LENGTH_SHORT).show()
//                    Log.e("PRAYER_API", "API Call failed: ${t.message}")
//                }
//            })
//    }
//
//    private fun convertToCalendar(time: String): Calendar {
//        val timeParts = time.split(":")
//        val calendar = Calendar.getInstance()
//        calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
//        calendar.set(Calendar.MINUTE, timeParts[1].toInt())
//        calendar.set(Calendar.SECOND, 0)
//        return calendar
//    }
//
//    private fun setReminder(calendar: Calendar, message: String) {
//        try {
//            val intent = Intent(this, ReminderReceiver::class.java)
//            intent.putExtra("reminder_message", message)
//
//            val pendingIntent = PendingIntent.getBroadcast(
//                this,
//                message.hashCode(),
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//
//            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
//
//            // For Android versions below Android 12 (API 30), no special permission is required
//            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
//                alarmManager.setExactAndAllowWhileIdle(
//                    AlarmManager.RTC_WAKEUP,
//                    calendar.timeInMillis,
//                    pendingIntent
//                )
//            }
//            // For Android 12 and above, ensure exact alarm permission is granted
//            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                if (alarmManager.canScheduleExactAlarms()) {
//                    alarmManager.setExactAndAllowWhileIdle(
//                        AlarmManager.RTC_WAKEUP,
//                        calendar.timeInMillis,
//                        pendingIntent
//                    )
//                } else {
//                    // If permission is not granted, show a Toast message or prompt the user
//                    Toast.makeText(this, "Permission to schedule exact alarms is denied.", Toast.LENGTH_SHORT).show()
//                }
//            }
//        } catch (e: SecurityException) {
//            // Handle case where app doesn't have permission to schedule exact alarms
//            Toast.makeText(this, "Permission to schedule exact alarms is denied.", Toast.LENGTH_SHORT).show()
//            Log.e("ReminderActivity", "Error setting alarm: ${e.message}")
//        }
//    }
//}
