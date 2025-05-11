package com.example.liveinpeace.ui.reminder

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
import androidx.appcompat.app.AppCompatActivity
import com.example.liveinpeace.R
import com.example.liveinpeace.api.RetrofitInstance
import com.example.liveinpeace.model.SalatTimesResponse
import com.example.liveinpeace.ui.receiver.ReminderReceiver
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ReminderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // Prompt the user to enable exact alarm scheduling in settings
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }

        val city = "Bandung"
        val country = "Indonesia"

        RetrofitInstance.api.getTimingsByCity(city, country)
            .enqueue(object : Callback<SalatTimesResponse> {
                override fun onResponse(call: Call<SalatTimesResponse>, response: Response<SalatTimesResponse>) {
                    if (response.isSuccessful) {
                        val timings = response.body()?.data?.timings
                        if (timings != null) {
                            Log.d("PRAYER_API", "Fajr: ${timings.Fajr}, Maghrib: ${timings.Maghrib}")

                            val pagiTime = timings.Fajr // Dzikir Pagi based on Fajr time
                            val petangTime = timings.Maghrib // Dzikir Petang based on Maghrib time

                            pagiTime?.let {
                                val time = convertToCalendar(it)
                                setReminder(time, "Waktunya Dzikir Pagi!")
                            }

                            petangTime?.let {
                                val time = convertToCalendar(it)
                                setReminder(time, "Waktunya Dzikir Petang!")
                            }
                        } else {
                            Log.e("PRAYER_API", "Timings not found in the response!")
                        }
                    } else {
                        Log.e("PRAYER_API", "Response not successful: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<SalatTimesResponse>, t: Throwable) {
                    Toast.makeText(this@ReminderActivity, "Failed to fetch salat times", Toast.LENGTH_SHORT).show()
                    Log.e("PRAYER_API", "API Call failed: ${t.message}")
                }
            })
    }

    private fun convertToCalendar(time: String): Calendar {
        val timeParts = time.split(":")
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
        calendar.set(Calendar.MINUTE, timeParts[1].toInt())
        calendar.set(Calendar.SECOND, 0)
        return calendar
    }

    private fun setReminder(calendar: Calendar, message: String) {
        try {
            val intent = Intent(this, ReminderReceiver::class.java)
            intent.putExtra("reminder_message", message)

            val pendingIntent = PendingIntent.getBroadcast(
                this,
                message.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

            // For Android versions below Android 12 (API 30), no special permission is required
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
            // For Android 12 and above, ensure exact alarm permission is granted
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    // If permission is not granted, show a Toast message or prompt the user
                    Toast.makeText(this, "Permission to schedule exact alarms is denied.", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: SecurityException) {
            // Handle case where app doesn't have permission to schedule exact alarms
            Toast.makeText(this, "Permission to schedule exact alarms is denied.", Toast.LENGTH_SHORT).show()
            Log.e("ReminderActivity", "Error setting alarm: ${e.message}")
        }
    }
}
