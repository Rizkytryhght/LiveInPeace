package com.example.liveinpeace.ui.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.liveinpeace.R
import com.example.liveinpeace.ui.reminder.ReminderActivity

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "prayer_reminder_channel"
        const val TAG = "ReminderReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("reminder_message") ?: "Waktu beribadah telah tiba"
        val prayerType = intent.getStringExtra(ReminderActivity.EXTRA_PRAYER_TYPE)
            ?: ReminderActivity.PRAYER_FAJR // Default to Fajr if not specified

        // Create notification
        showNotification(context, message, prayerType)

        Log.d(TAG, "Received broadcast for prayer reminder: $prayerType - $message")
    }

    private fun showNotification(context: Context, message: String, prayerType: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        createNotificationChannel(notificationManager, context)

        // Intent to open app when notification is tapped
        val contentIntent = Intent(context, ReminderActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Determine title based on prayer type
        val title = when (prayerType) {
            ReminderActivity.PRAYER_FAJR -> "Pengingat Sholat Subuh"
            ReminderActivity.PRAYER_DHUHR -> "Pengingat Sholat Dzuhur"
            ReminderActivity.PRAYER_ASR -> "Pengingat Sholat Ashar"
            ReminderActivity.PRAYER_MAGHRIB -> "Pengingat Sholat Maghrib"
            ReminderActivity.PRAYER_ISHA -> "Pengingat Sholat Isya"
            ReminderActivity.DZIKIR_PAGI -> "Pengingat Dzikir Pagi"
            ReminderActivity.DZIKIR_PETANG -> "Pengingat Dzikir Petang"
            else -> "Pengingat Ibadah"
        }

        // Build the notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build() // Custom sound is set in the channel for Android 8.0+

        // Show the notification with a unique ID based on prayer type
        notificationManager.notify(prayerType.hashCode(), notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager, context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = Uri.parse("android.resource://${context.packageName}/${R.raw.notif}") // Reference your sound file
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Prayer Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notification channel for prayer time reminders"
                enableLights(true)
                enableVibration(true)
                setSound(soundUri, audioAttributes) // Set custom sound
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}