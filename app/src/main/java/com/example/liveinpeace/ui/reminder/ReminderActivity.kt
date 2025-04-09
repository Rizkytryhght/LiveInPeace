package com.example.liveinpeace.ui.reminder

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.liveinpeace.R
import com.example.liveinpeace.ui.receiver.ReminderReceiver
import java.util.Calendar

@SuppressLint("UseSwitchCompatOrMaterialCode")
class ReminderActivity : AppCompatActivity() {

    private lateinit var switchPagi: Switch
    private lateinit var switchPetang: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        // Minta izin notifikasi (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // Ajak user buat buka pengaturan dan aktifkan
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }

        switchPagi = findViewById(R.id.switchDzikirPagi)
        switchPetang = findViewById(R.id.switchDzikirPetang)

        val sharedPref = getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)

        // Set saved state
        switchPagi.isChecked = sharedPref.getBoolean("pagi", false)
        switchPetang.isChecked = sharedPref.getBoolean("petang", false)

        switchPagi.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("pagi", isChecked).apply()
            if (isChecked) setReminder(7, 0, "Waktunya Dzikir Pagi!")
            else cancelReminder("pagi")
        }

        switchPetang.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("petang", isChecked).apply()
            if (isChecked) setReminder(16, 30, "Waktunya Dzikir Petang!")
            else cancelReminder("petang")
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setReminder(hour: Int, minute: Int, message: String) {
        val intent = Intent(this, ReminderReceiver::class.java)
        intent.putExtra("reminder_message", message)

        val requestCode = message.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DATE, 1)
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    private fun cancelReminder(key: String) {
        val message = if (key == "pagi") "Waktunya Dzikir Pagi!" else "Waktunya Dzikir Petang!"
        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            message.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}