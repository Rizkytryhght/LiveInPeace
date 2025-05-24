package com.example.liveinpeace.ui.note

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.liveinpeace.R
import com.example.liveinpeace.data.Note
import com.example.liveinpeace.data.repository.NoteRepository
import com.example.liveinpeace.ui.features.FeaturesListActivity
import com.example.liveinpeace.ui.home.HomeActivity
import com.example.liveinpeace.ui.profile.ProfileActivity // 🔥 Import ini
import com.example.liveinpeace.viewModel.NoteViewModel
import com.example.liveinpeace.viewModel.NoteViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView // 🔥
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NoteActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var tagChipGroup: ChipGroup
    private lateinit var adapter: NoteAdapter
    private lateinit var noteViewModel: NoteViewModel
    private var allNotes = listOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        recyclerView = findViewById(R.id.notesRecyclerView)
        searchEditText = findViewById(R.id.searchEditText)
        tagChipGroup = findViewById(R.id.chipGroup)
        val addNoteButton: FloatingActionButton = findViewById(R.id.addNoteButton)

        val repository = NoteRepository()
        val viewModelFactory = NoteViewModelFactory(repository)

        noteViewModel = ViewModelProvider(this, viewModelFactory)[NoteViewModel::class.java]

        // Di dalam NoteActivity, bagian adapter diubah jadi:
        adapter = NoteAdapter(mutableListOf(),
            { note ->
                val intent = Intent(this@NoteActivity, NoteDetailActivity::class.java)
                intent.putExtra("note_id", note.id)
                intent.putExtra("title", note.title)
                intent.putExtra("content", note.content)
                intent.putExtra("tag", note.tag)
                intent.putExtra("date", note.date)
                intent.putExtra("time", note.time)
                intent.putExtra("is_new", false)
                startActivityForResult(intent, REQUEST_CODE_EDIT)
            },
            { note ->
                noteViewModel.deleteNote(note)
                Toast.makeText(this, "Catatan dihapus", Toast.LENGTH_SHORT).show()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        observeRealtimeNotes()

        addNoteButton.setOnClickListener {
            val intent = Intent(this, NoteDetailActivity::class.java)
            intent.putExtra("is_new", true)
            startActivityForResult(intent, REQUEST_CODE_ADD)
        }

        searchEditText.addTextChangedListener {
            filterNotes(it.toString())
        }

        setupChips()

        // 🔥 Bottom navigation setup
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_notes

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_notes -> true // udah di sini
                R.id.nav_features -> {
                    startActivity(Intent(this, FeaturesListActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun observeRealtimeNotes() {
        noteViewModel.realtimeNotes.observe(this) { notes ->
            allNotes = notes
            val sortedNotes = notes.sortedByDescending { it.date + it.time } // urutkan berdasarkan waktu
            adapter.updateList(sortedNotes)
        }
    }

    private fun filterNotes(query: String) {
        val filteredList = allNotes.filter {
            it.title.contains(query, true) || it.content.contains(query, true)
        }
        adapter.updateList(filteredList)
    }

    private fun filterByTag(tag: String) {
        if (tag == "Semua") {
            adapter.updateList(allNotes)
        } else {
            val filteredList = allNotes.filter { it.tag == tag }
            adapter.updateList(filteredList)
        }
    }

    private fun setupChips() {
        val tagList = listOf("Semua", "Motivasi", "Belajar", "Kerja")
        tagChipGroup.removeAllViews()

        tagList.forEach { tag ->
            val chip = Chip(this)
            chip.text = tag
            chip.isCheckable = true

            if (tag == "Semua") {
                chip.isChecked = true
            }

            chip.setOnClickListener {
                for (i in 0 until tagChipGroup.childCount) {
                    val c = tagChipGroup.getChildAt(i) as Chip
                    if (c != chip) {
                        c.isChecked = false
                    }
                }
                filterByTag(tag)
            }
            tagChipGroup.addView(chip)
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val noteId = data.getStringExtra("id") ?: ""
            val title = data.getStringExtra("title") ?: ""
            val content = data.getStringExtra("content") ?: ""
            val date = data.getStringExtra("date") ?: ""
            val time = data.getStringExtra("time") ?: ""
            val tag = data.getStringExtra("tag") ?: ""

            val note = Note(noteId, title, content, date, time, tag)

            if (requestCode == REQUEST_CODE_ADD) {
                noteViewModel.insertNote(note)
            } else if (requestCode == REQUEST_CODE_EDIT) {
                noteViewModel.updateNote(note)
            }
            observeRealtimeNotes()
        }
    }

    companion object {
        const val REQUEST_CODE_ADD = 1001
        const val REQUEST_CODE_EDIT = 1002
    }
}