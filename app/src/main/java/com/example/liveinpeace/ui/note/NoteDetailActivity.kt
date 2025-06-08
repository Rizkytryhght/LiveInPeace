package com.example.liveinpeace.ui.note

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import java.text.SimpleDateFormat
import java.util.*

class NoteDetailActivity : ComponentActivity() {
    private var isNewNote = true
    private var noteId = ""
    private var selectedTag = "Semua"
    private var currentDate = ""
    private var currentTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get intent data (same as original activity)
        isNewNote = intent.getBooleanExtra("is_new", true)

        val initialTitle: String
        val initialContent: String

        if (!isNewNote) {
            noteId = intent.getStringExtra("note_id") ?: UUID.randomUUID().toString()
            initialTitle = intent.getStringExtra("title") ?: ""
            initialContent = intent.getStringExtra("content") ?: ""
            selectedTag = intent.getStringExtra("tag") ?: "Semua"
            currentDate = intent.getStringExtra("date") ?: ""
            currentTime = intent.getStringExtra("time") ?: ""

            // Jika data kosong, generate ulang
            if (currentDate.isEmpty() || currentTime.isEmpty()) {
                setupCurrentDateTime()
            }
        } else {
            noteId = UUID.randomUUID().toString()
            initialTitle = ""
            initialContent = ""
            setupCurrentDateTime()
        }

        setContent {
            MaterialTheme {
                NoteDetailScreen(
                    isNewNote = isNewNote,
                    noteId = noteId,
                    initialTitle = initialTitle,
                    initialContent = initialContent,
                    initialTag = selectedTag,
                    initialDate = currentDate,
                    initialTime = currentTime,
                    onNavigateBack = { finish() },
                    onNoteSaved = { note ->
                        val resultIntent = Intent().apply {
                            putExtra("id", note.id)
                            putExtra("title", note.title)
                            putExtra("content", note.content)
                            putExtra("date", note.date)
                            putExtra("time", note.time)
                            putExtra("tag", note.tag)
                        }
                        setResult(RESULT_OK, resultIntent)
                    }
                )
            }
        }
    }

    private fun setupCurrentDateTime() {
        val calendar = Calendar.getInstance()
        val rawDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        val now = calendar.time
        currentDate = rawDateFormat.format(now)
        currentTime = timeFormat.format(now)
    }
}