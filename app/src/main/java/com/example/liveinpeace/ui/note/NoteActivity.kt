package com.example.liveinpeace.ui.note

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.liveinpeace.R
import com.example.liveinpeace.data.Note
import com.example.liveinpeace.data.repository.NoteRepository
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

        adapter = NoteAdapter(mutableListOf(),
            { note ->
                val intent = Intent(this@NoteActivity, NoteDetailActivity::class.java)
                intent.putExtra("note_id", note.id)
                intent.putExtra("is_new", false)
                startActivityForResult(intent, REQUEST_CODE_EDIT)
            },
            { note ->
                noteViewModel.deleteNote(note)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadNotes()

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
                R.id.nav_notes -> {
                    // Halaman ini sedang dibuka
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

    private fun loadNotes() {
        noteViewModel.getAllNotes().observe(this) { notes ->
            allNotes = notes
            adapter.updateList(notes)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val noteId = data.getStringExtra("id") ?: ""
            val title = data.getStringExtra("title") ?: ""
            val content = data.getStringExtra("content") ?: ""
            val date = data.getStringExtra("date") ?: ""
            val time = data.getStringExtra("time") ?: ""
            val tag = data.getStringExtra("tag") ?: ""

            val note = Note(noteId, title, content, date, "Monday", time, tag)

            if (requestCode == REQUEST_CODE_ADD) {
                noteViewModel.insertNote(note)
            } else if (requestCode == REQUEST_CODE_EDIT) {
                noteViewModel.updateNote(note)
            }
            loadNotes()
        }
    }

    companion object {
        const val REQUEST_CODE_ADD = 1001
        const val REQUEST_CODE_EDIT = 1002
    }
}