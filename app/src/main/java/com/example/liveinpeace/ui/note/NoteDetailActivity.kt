package com.example.liveinpeace.ui.note

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.liveinpeace.R
import com.example.liveinpeace.data.Note
import com.example.liveinpeace.data.repository.NoteRepository
import com.example.liveinpeace.viewModel.NoteViewModel
import com.example.liveinpeace.viewModel.NoteViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.text.SimpleDateFormat
import java.util.*

class NoteDetailActivity : AppCompatActivity() {
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var backButton: ImageButton
    private lateinit var saveButton: Button
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var dateTextView: TextView
    private lateinit var tagChipGroup: ChipGroup

    private var isNewNote = true
    private var noteId = ""
    private var selectedTag = "Semua"
    private var currentDate = ""
    private var currentTime = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        val repository = NoteRepository()
        val factory = NoteViewModelFactory(repository)
        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        initViews()
        setupChips()
        setupBackButton()
        setupSaveButton()

        isNewNote = intent.getBooleanExtra("is_new", true)

        if (!isNewNote) {
            noteId = intent.getStringExtra("note_id") ?: UUID.randomUUID().toString()
            titleEditText.setText(intent.getStringExtra("title"))
            contentEditText.setText(intent.getStringExtra("content"))
            selectedTag = intent.getStringExtra("tag") ?: "Semua"

            currentDate = intent.getStringExtra("date") ?: ""
            currentTime = intent.getStringExtra("time") ?: ""

            // Jika data kosong, generate ulang
            if (currentDate.isEmpty() || currentTime.isEmpty()) {
                setupCurrentDateTime()
            }

            dateTextView.text = getDisplayDate()
            updateChipsSelection(selectedTag)
        } else {
            noteId = UUID.randomUUID().toString()
            setupCurrentDateTime()
            dateTextView.text = getDisplayDate()
        }
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        saveButton = findViewById(R.id.saveButton)
        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)
        dateTextView = findViewById(R.id.dateTextView)
        tagChipGroup = findViewById(R.id.tagChipGroup)
    }

    private fun setupChips() {
        val tagList = listOf("Semua", "Motivasi", "Belajar", "Kerja")
        tagChipGroup.removeAllViews()

        tagList.forEach { tag ->
            val chip = Chip(this)
            chip.text = tag
            chip.isCheckable = true
            chip.isChecked = tag == selectedTag

            chip.setOnClickListener {
                selectedTag = tag
                updateChipsSelection(tag)
            }

            tagChipGroup.addView(chip)
        }
    }

    private fun updateChipsSelection(selectedTag: String) {
        for (i in 0 until tagChipGroup.childCount) {
            val chip = tagChipGroup.getChildAt(i) as Chip
            chip.isChecked = chip.text == selectedTag
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

    private fun getDisplayDate(): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(currentDate)

            val displayDate = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(date ?: Date())
            val dayName = SimpleDateFormat("EEEE", Locale("id", "ID")).format(date ?: Date())
            val timeDisplay = currentTime // misal: 11:15 AM

            "$displayDate ($dayName), $timeDisplay"
        } catch (e: Exception) {
            currentDate
        }
    }

    private fun setupBackButton() {
        backButton.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            saveNote()
        }
    }

    private fun saveNote() {
        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString().trim()

        if (title.isEmpty()) {
            titleEditText.error = "Judul tidak boleh kosong"
            return
        }

        val note = Note(
            id = noteId,
            title = title,
            content = content,
            date = currentDate,
            time = currentTime,
            tag = selectedTag
        )

        if (isNewNote) {
            noteViewModel.insertNote(note)
            Toast.makeText(this, "Catatan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
        } else {
            noteViewModel.updateNote(note)
            Toast.makeText(this, "Catatan berhasil diperbarui", Toast.LENGTH_SHORT).show()
        }

        val resultIntent = Intent().apply {
            putExtra("id", note.id)
            putExtra("title", note.title)
            putExtra("content", note.content)
            putExtra("date", note.date)
            putExtra("time", note.time)
            putExtra("tag", note.tag)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun showConfirmationDialog() {
        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString().trim()

        if (title.isNotEmpty() || content.isNotEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Apakah anda ingin menyimpan perubahan?")
                .setPositiveButton("Simpan") { _, _ -> saveNote() }
                .setNegativeButton("Hapus") { _, _ ->
                    if (!isNewNote) {
                        noteViewModel.deleteNote(
                            Note(id = noteId, title = title, content = content, date = currentDate, time = currentTime, tag = selectedTag)
                        )
                        Toast.makeText(this, "Catatan dihapus", Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }
                .setNeutralButton("Batal") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        } else {
            finish()
        }
    }
}