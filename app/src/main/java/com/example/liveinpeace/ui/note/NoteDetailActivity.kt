package com.example.liveinpeace.ui.note

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.liveinpeace.R
import com.example.liveinpeace.data.Note
import com.example.liveinpeace.viewModel.NoteViewModel
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
    private var currentDay = ""
    private var currentTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        // Initialize ViewModel
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        initViews()
        setupCurrentDateTime()
        setupChips()
        setupBackButton()
        setupSaveButton()

        // Get data from intent
        isNewNote = intent.getBooleanExtra("is_new", true)

        if (!isNewNote) {
            // Editing existing note
            noteId = intent.getStringExtra("note_id") ?: ""
            titleEditText.setText(intent.getStringExtra("title"))
            contentEditText.setText(intent.getStringExtra("content"))
            selectedTag = intent.getStringExtra("tag") ?: "Semua"

            // If date and time were passed, use them instead of current
            val passedDate = intent.getStringExtra("date")
            if (!passedDate.isNullOrEmpty()) {
                currentDate = passedDate
                currentDay = intent.getStringExtra("day") ?: currentDay
                currentTime = intent.getStringExtra("time") ?: currentTime
                dateTextView.text = "$currentDate $currentDay"
            }

            updateChipsSelection(selectedTag)
        } else {
            // Generate a temporary ID for new note
            noteId = UUID.randomUUID().toString()
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

    private fun setupCurrentDateTime() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale("in", "ID"))
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        currentDate = dateFormat.format(calendar.time)
        currentDay = dayFormat.format(calendar.time).uppercase()
        currentTime = timeFormat.format(calendar.time)

        dateTextView.text = "$currentDate $currentDay"
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
            day = currentDay,
            time = currentTime,
            tag = selectedTag
        )

        if (isNewNote) {
            noteViewModel.addNote(note)
            Toast.makeText(this, "Catatan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
        } else {
            noteViewModel.editNote(note)
            Toast.makeText(this, "Catatan berhasil diperbarui", Toast.LENGTH_SHORT).show()
        }

        // Return result to calling activity
        val resultIntent = Intent()
        resultIntent.putExtra("id", noteId)
        resultIntent.putExtra("title", title)
        resultIntent.putExtra("content", content)
        resultIntent.putExtra("date", currentDate)
        resultIntent.putExtra("day", currentDay)
        resultIntent.putExtra("time", currentTime)
        resultIntent.putExtra("tag", selectedTag)

        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun showConfirmationDialog() {
        val dialogBuilder = AlertDialog.Builder(this)

        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString().trim()

        // Check if anything has been entered
        if (title.isNotEmpty() || content.isNotEmpty()) {
            dialogBuilder.setTitle("Konfirmasi")
                .setMessage("Apakah anda ingin menyimpan perubahan?")
                .setPositiveButton("Simpan") { _, _ ->
                    saveNote()
                }
                .setNegativeButton("Hapus") { _, _ ->
                    if (!isNewNote) {
                        // Only allow deletion of existing notes
                        noteViewModel.deleteNote(Note(
                            id = noteId,
                            title = title,
                            content = content,
                            date = currentDate,
                            day = currentDay,
                            time = currentTime,
                            tag = selectedTag
                        ))
                        Toast.makeText(this, "Catatan dihapus", Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }
                .setNeutralButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
            dialogBuilder.create().show()
        } else {
            // Nothing entered, just go back
            finish()
        }
    }

//    override fun onBackPressed() {
//        showConfirmationDialog()
//    }
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
