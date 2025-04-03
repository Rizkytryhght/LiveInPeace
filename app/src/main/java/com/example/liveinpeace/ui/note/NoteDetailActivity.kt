package com.example.liveinpeace.ui.note

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.liveinpeace.R
import com.example.liveinpeace.data.Note
import com.example.liveinpeace.data.repository.NoteRepository
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteDetailActivity : AppCompatActivity() {

    private lateinit var noteRepository: NoteRepository // Declare the repository
    private lateinit var backButton: ImageButton
    private lateinit var saveButton: Button
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var dateTextView: TextView
    private lateinit var tagChipGroup: ChipGroup
    private var isNewNote = false
    private var noteId = ""
    private var selectedTag = "Semua"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        // Initialize repository
        noteRepository = NoteRepository()

        initViews()
        setupCurrentDate()
        setupChips()
        setupBackButton()
        setupSaveButton()

        isNewNote = intent.getBooleanExtra("is_new", false)

        if (!isNewNote) {
            noteId = intent.getStringExtra("note_id") ?: ""
            titleEditText.setText(intent.getStringExtra("note_title"))
            contentEditText.setText(intent.getStringExtra("note_content"))
            selectedTag = intent.getStringExtra("note_tag") ?: "Semua"
            tagChipGroup.check(getChipIdFromTag(selectedTag))
        }
    }

    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()
            val date = dateTextView.text.toString()
            val time = SimpleDateFormat("hh:mm a", Locale("en", "US")).format(Date())

            // Create the updatedNote object
            val updatedNote = Note(noteId, title, content, date, "Monday", time, selectedTag)

            // Call saveNote or update depending on isNewNote
            CoroutineScope(Dispatchers.Main).launch {
                if (isNewNote) {
                    // Insert new note
                    val success = noteRepository.insert(updatedNote)
                    if (success) {
                        showToast("Note saved!")
                    } else {
                        showToast("Failed to save note")
                    }
                } else {
                    // Update existing note
                    val success = noteRepository.update(updatedNote)
                    if (success) {
                        showToast("Note updated!")
                    } else {
                        showToast("Failed to update note")
                    }
                }

                // Pass data back to the calling activity
                val resultIntent = Intent().apply {
                    putExtra("id", noteId)
                    putExtra("title", title)
                    putExtra("content", content)
                    putExtra("date", date)
                    putExtra("time", time)
                    putExtra("day", "Monday")
                    putExtra("tag", selectedTag)
                }

                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        saveButton = findViewById(R.id.saveButton)
        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)
        dateTextView = findViewById(R.id.dateTextView)
        tagChipGroup = findViewById(R.id.tagChipGroup)
    }

    private fun setupCurrentDate() {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale("en", "US"))
        val currentDate = dateFormat.format(Date())
        dateTextView.text = currentDate
    }

    private fun setupChips() {
        val tags = listOf("Semua", "Motivasi", "Belajar", "Kerja")

        tags.forEach { tag ->
            val chip = Chip(this).apply {
                text = tag
                isCheckable = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) selectedTag = tag
                }
            }
            tagChipGroup.addView(chip)
        }
    }

    private fun getChipIdFromTag(tag: String): Int {
        return when (tag) {
            "Motivasi" -> R.id.chipMotivation
            "Belajar" -> R.id.chipStudy
            "Kerja" -> R.id.chipWork
            else -> R.id.chipAll
        }
    }

    private fun setupBackButton() {
        backButton.setOnClickListener {
            finish()
        }
    }
}



//import android.app.AlertDialog
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageButton
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import com.example.liveinpeace.R
//import com.google.android.material.chip.Chip
//import com.google.android.material.chip.ChipGroup
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//
//class NoteDetailActivity : AppCompatActivity() {
//    private lateinit var backButton: ImageButton
//    private lateinit var saveButton: Button
//    private lateinit var titleEditText: EditText
//    private lateinit var contentEditText: EditText
//    private lateinit var dateTextView: TextView
//    private lateinit var tagChipGroup: ChipGroup
//    private var isNewNote = false
//    private var noteId = ""
//    private var selectedTag = "Semua"
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_note_detail)
//
//        initViews()
//        setupCurrentDate()
//        setupChips()
//        setupBackButton()
//        setupSaveButton()
//
//        isNewNote = intent.getBooleanExtra("is_new", false)
//
//        if (!isNewNote) {
//            noteId = intent.getStringExtra("note_id") ?: ""
//            titleEditText.setText(intent.getStringExtra("note_title"))
//            contentEditText.setText(intent.getStringExtra("note_content"))
//            selectedTag = intent.getStringExtra("note_tag") ?: "Semua"
//            updateChipsSelection(selectedTag)
//        }
//    }
//
//    private fun initViews() {
//        backButton = findViewById(R.id.backButton)
//        saveButton = findViewById(R.id.saveButton)
//        titleEditText = findViewById(R.id.titleEditText)
//        contentEditText = findViewById(R.id.contentEditText)
//        dateTextView = findViewById(R.id.dateTextView)
//        tagChipGroup = findViewById(R.id.tagChipGroup)
//    }
//
//    private fun setupCurrentDate() {
//        val date = Date()
//        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
//        val dayFormat = SimpleDateFormat("EEEE", Locale("in", "ID")).format(date).uppercase()
//        dateTextView.text = "${dateFormat.format(date)} $dayFormat"
//    }
//
//    private fun setupChips() {
//        val tagList = listOf("Semua", "Motivasi", "Belajar", "Kerja")
//
//        tagChipGroup.removeAllViews()
//
//        tagList.forEach { tag ->
//            val chip = Chip(this)
//            chip.text = tag
//            chip.isCheckable = true
//            chip.isChecked = tag == selectedTag
//
//            chip.setOnClickListener {
//                selectedTag = tag
//                updateChipsSelection(tag)
//            }
//
//            tagChipGroup.addView(chip)
//        }
//    }
//
//    private fun updateChipsSelection(selectedTag: String) {
//        for (i in 0 until tagChipGroup.childCount) {
//            val chip = tagChipGroup.getChildAt(i) as Chip
//            chip.isChecked = chip.text == selectedTag
//        }
//    }
//
//    private fun setupBackButton() {
//        backButton.setOnClickListener {
//            showConfirmationDialog()
//        }
//    }
//
//    private fun setupSaveButton() {
//        saveButton.setOnClickListener {
//            val title = titleEditText.text.toString()
//            val content = contentEditText.text.toString()
//
//            if (title.isNotEmpty()) {
//                val resultIntent = Intent()
//                resultIntent.putExtra("is_new", isNewNote)
//                resultIntent.putExtra("note_id", noteId)
//                resultIntent.putExtra("note_title", title)
//                resultIntent.putExtra("note_content", content)
//                resultIntent.putExtra("note_tag", selectedTag)
//
//                setResult(RESULT_OK, resultIntent)
//                finish()
//            } else {
//                titleEditText.error = "Judul tidak boleh kosong"
//            }
//        }
//    }
//
//    private fun showConfirmationDialog() {
//        val dialogBuilder = AlertDialog.Builder(this)
//
//        if (!isNewNote) {
//            dialogBuilder.setTitle("Konfirmasi")
//                .setMessage("Apakah anda ingin menyimpan perubahan?")
//                .setPositiveButton("Simpan") { _, _ ->
//                    saveButton.performClick()
//                }
//                .setNegativeButton("Hapus") { _, _ ->
//                    val resultIntent = Intent()
//                    resultIntent.putExtra("note_id", noteId)
//                    setResult(2, resultIntent) // 2 for delete
//                    finish()
//                }
//                .setNeutralButton("Batal") { dialog, _ ->
//                    dialog.dismiss()
//                }
//        } else {
//            dialogBuilder.setTitle("Konfirmasi")
//                .setMessage("Apakah anda ingin menyimpan catatan ini?")
//                .setPositiveButton("Simpan") { _, _ ->
//                    saveButton.performClick()
//                }
//                .setNegativeButton("Batal") { _, _ ->
//                    finish()
//                }
//        }
//
//        dialogBuilder.create().show()
//    }
//}

//    override fun onBackPressed() {
//        showConfirmationDialog()
//    }
