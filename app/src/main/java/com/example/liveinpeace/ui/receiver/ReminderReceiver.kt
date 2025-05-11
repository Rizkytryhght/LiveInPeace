package com.example.liveinpeace.ui.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.util.Log

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("reminder_message")
        // Tampilkan notifikasi atau aksi lainnya
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

        // Log untuk debugging
        Log.d("ReminderReceiver", "Received message: $message")
    }
}
