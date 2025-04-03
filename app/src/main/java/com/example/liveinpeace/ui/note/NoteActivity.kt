package com.example.liveinpeace.ui.note

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.liveinpeace.R
import com.example.liveinpeace.data.Note
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class NoteActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var tagChipGroup: ChipGroup
    private lateinit var adapter: NoteAdapter
    private var noteList = mutableListOf<Note>()
    private var selectedTag: String = "Semua"
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        recyclerView = findViewById(R.id.noteRecyclerView)
        searchEditText = findViewById(R.id.searchEditText)
        tagChipGroup = findViewById(R.id.chipGroup)
        val addNoteButton: FloatingActionButton = findViewById(R.id.addNoteButton)

        // Initialize Firebase database
        database = FirebaseDatabase.getInstance().reference.child("notes")

        adapter = NoteAdapter(noteList, object : NoteAdapter.OnItemClickListener {
            override fun onItemClick(note: Note) {
                val intent = Intent(this@NoteActivity, NoteDetailActivity::class.java)
                intent.putExtra("id", note.id)
                intent.putExtra("title", note.title)
                intent.putExtra("content", note.content)
                intent.putExtra("date", note.date)
                intent.putExtra("day", note.day)
                intent.putExtra("tag", note.tag)
                intent.putExtra("isNew", false)
                startActivityForResult(intent, REQUEST_CODE_EDIT)
            }
        }, { note ->
            // Implement delete function using Firebase
            database.child(note.id).removeValue()
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Load notes from Firebase
        loadNotes()

        addNoteButton.setOnClickListener {
            val intent = Intent(this, NoteDetailActivity::class.java)
            intent.putExtra("isNew", true)
            startActivityForResult(intent, REQUEST_CODE_ADD)
        }

        searchEditText.addTextChangedListener {
            filterNotes(it.toString(), selectedTag)
        }

        setupChips()
    }

    private fun loadNotes() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                noteList.clear()
                for (noteSnapshot in snapshot.children) {
                    val note = noteSnapshot.getValue(Note::class.java)
                    if (note != null) {
                        noteList.add(note)
                    }
                }
                sortNotes()
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun sortNotes() {
        noteList.sortByDescending { it.date }
    }

    private fun filterNotes(query: String, tag: String) {
        val filteredList = noteList.filter {
            (it.title.contains(query, true) || it.content.contains(query, true)) &&
                    (tag == "Semua" || it.tag == tag)
        }
        adapter.updateList(filteredList) // Memperbarui daftar di adapter
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
                filterNotes(searchEditText.text.toString(), selectedTag)
            }

            tagChipGroup.addView(chip)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val id = data.getStringExtra("id") ?: ""
            val title = data.getStringExtra("title") ?: ""
            val content = data.getStringExtra("content") ?: ""
            val date = data.getStringExtra("date") ?: ""
            val day = data.getStringExtra("day") ?: ""
            val time = data.getStringExtra("time") ?: ""
            val tag = data.getStringExtra("tag") ?: ""

            val note = Note(id, title, content, date, day, time, tag)
            if (requestCode == REQUEST_CODE_ADD) {
                // Add new note
                database.child(id).setValue(note)
            } else if (requestCode == REQUEST_CODE_EDIT) {
                // Update existing note
                database.child(id).setValue(note)
            }
        }
    }

    companion object {
        const val REQUEST_CODE_ADD = 1001
        const val REQUEST_CODE_EDIT = 1002
    }
}


//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import android.widget.EditText
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.widget.addTextChangedListener
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.liveinpeace.R
//import com.example.liveinpeace.data.Note
//import com.google.android.material.chip.Chip
//import com.google.android.material.chip.ChipGroup
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import java.text.SimpleDateFormat
//import java.util.*
//
//class NoteActivity : AppCompatActivity() {
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var searchEditText: EditText
//    private lateinit var tagChipGroup: ChipGroup
//    private lateinit var adapter: NoteAdapter
//    private var noteList = mutableListOf<Note>()
//    private var selectedTag: String = "Semua"
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_note)
//
//        recyclerView = findViewById(R.id.noteRecyclerView)
//        searchEditText = findViewById(R.id.searchEditText)
//        tagChipGroup = findViewById(R.id.chipGroup)
//        val addNoteButton: FloatingActionButton = findViewById(R.id.addNoteButton)
//
//        adapter = NoteAdapter(noteList, object : NoteAdapter.OnItemClickListener {
//            override fun onItemClick(note: Note) {
//                val intent = Intent(this@NoteActivity, NoteDetailActivity::class.java)
//                intent.putExtra("id", note.id)
//                intent.putExtra("title", note.title)
//                intent.putExtra("content", note.content)
//                intent.putExtra("date", note.date)
//                intent.putExtra("day", note.day)
//                intent.putExtra("tag", note.tag)
//                intent.putExtra("isNew", false)
//                startActivityForResult(intent, REQUEST_CODE_EDIT)
//            }
//        }, { note ->
//            adapter.removeNote(note) // Tambahkan ini untuk menghapus catatan
//        })
//
//
////        recyclerView.layoutManager = LinearLayoutManager(this)
////        adapter = NoteAdapter(noteList, object : NoteAdapter.OnItemClickListener {
////            override fun onItemClick(note: Note) {
////                val intent = Intent(this@NoteActivity, NoteDetailActivity::class.java)
////                intent.putExtra("id", note.id)
////                intent.putExtra("title", note.title)
////                intent.putExtra("content", note.content)
////                intent.putExtra("date", note.date)
////                intent.putExtra("day", note.day) // Include day in intent
////                intent.putExtra("tag", note.tag)
////                intent.putExtra("isNew", false)
////                startActivityForResult(intent, REQUEST_CODE_EDIT)
////            }
////        })
//        recyclerView.adapter = adapter
//
//        addSampleNotes()
//        setupChips()
//
//        searchEditText.addTextChangedListener {
//            filterNotes(it.toString(), selectedTag)
//        }
//
//        addNoteButton.setOnClickListener {
//            val intent = Intent(this, NoteDetailActivity::class.java)
//            intent.putExtra("isNew", true)
//
//            // Add current date and day as default
//            val calendar = Calendar.getInstance()
//            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
//            val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
//
//            val currentDate = dateFormat.format(calendar.time)
//            val currentDay = dayFormat.format(calendar.time).uppercase()
//
//            intent.putExtra("date", currentDate)
//            intent.putExtra("day", currentDay)
//
//            startActivityForResult(intent, REQUEST_CODE_ADD)
//        }
//    }
//
//    private fun addSampleNotes() {
//        // Add sample notes with days included
//        noteList.add(Note(
//            "1",
//            "My Journal",
//            "Daily recap",
//            "Mar 20, 2025",
//            "KAMIS",
//            "11:00 AM",
//            "Semua",
//        ))
//        noteList.add(Note(
//            "2",
//            "Tugas Hari ini",
//            "Membuat aplikasi",
//            "Apr 02, 2024",
//            "RABU",
//            "12:30 PM",
//            "Belajar",
//        ))
//
//        // Sort by date, newest first
//        sortNotes()
//        adapter.notifyDataSetChanged()
//    }
//
//    private fun sortNotes() {
//        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
//        noteList.sortByDescending {
//            try {
//                dateFormat.parse(it.date)
//            } catch (e: Exception) {
//                Date(0) // Fallback for invalid dates
//            }
//        }
//    }
//
//    private fun filterNotes(query: String, tag: String) {
//        val filteredList = noteList.filter {
//            (it.title.contains(query, true) || it.content.contains(query, true)) &&
//                    (tag == "Semua" || it.tag == tag)
//        }
//        adapter.updateList(filteredList)
//    }
//
//    private fun setupChips() {
//        val tagList = listOf("Semua", "Motivasi", "Belajar", "Kerja")
//        tagChipGroup.removeAllViews()
//
//        tagList.forEach { tag ->
//            val chip = Chip(this)
//            chip.text = tag
//            chip.isCheckable = true
//            chip.isChecked = tag == selectedTag
//
//            // Set chip styles based on selection state
//            if (tag == selectedTag) {
//                chip.setChipBackgroundColorResource(android.R.color.holo_green_dark)
//                chip.setTextColor(resources.getColor(android.R.color.white, theme))
//            } else {
//                chip.setChipBackgroundColorResource(android.R.color.white)
//                chip.setTextColor(resources.getColor(android.R.color.holo_green_dark, theme))
//                chip.chipStrokeWidth = 1f
//                chip.setChipStrokeColorResource(if (tag == "Semua")android.R.color.holo_green_dark else android.R.color.white)
//            }
//
//            chip.setOnClickListener {
//                selectedTag = tag
//                filterNotes(searchEditText.text.toString(), selectedTag)
//
//                // Update all chips' styles
//                for (i in 0 until tagChipGroup.childCount) {
//                    val childChip = tagChipGroup.getChildAt(i) as Chip
//                    val isSelected = childChip.text == tag
//
//                    if (isSelected) {
//                        childChip.setChipBackgroundColorResource(android.R.color.holo_green_dark)
//                        childChip.setTextColor(resources.getColor(android.R.color.white, theme))
//                        childChip.chipStrokeWidth = 0f
//                    } else {
//                        childChip.setChipBackgroundColorResource(android.R.color.white)
//                        childChip.setTextColor(resources.getColor(android.R.color.white, theme))
//                        childChip.chipStrokeWidth = 1f
//                        childChip.setChipStrokeColorResource(if (childChip.text == "Semua")android.R.color.holo_green_dark else android.R.color.white)
//                    }
//                }
//            }
//            tagChipGroup.addView(chip)
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK && data != null) {
//            val id = data.getStringExtra("id") ?: UUID.randomUUID().toString()
//            val title = data.getStringExtra("title") ?: ""
//            val content = data.getStringExtra("content") ?: ""
//            val date = data.getStringExtra("date") ?: ""
//            val day = data.getStringExtra("day") ?: "" // Get day from result
//            val time = data.getStringExtra("time") ?: SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
//            val tag = data.getStringExtra("tag") ?: ""
//            val color = getColorForTag(tag) // Assign color based on tag
//            val isNew = data.getBooleanExtra("isNew", true)
//
//            if (isNew) {
//                noteList.add(Note(id, title, content, date, day, time, tag))
//            } else {
//                val index = noteList.indexOfFirst { it.id == id }
//                if (index != -1) {
//                    noteList[index] = Note(
//                        id,
//                        title,
//                        content,
//                        date,
//                        day,
//                        time,
//                        tag,
//                    )
//                }
//            }
//            sortNotes()
//            adapter.notifyDataSetChanged()
//        }
//    }
//
//    private fun getColorForTag(tag: String): String {
//        return when (tag) {
//            "Motivasi" -> "#7EFC9B" // Green
//            "Belajar" -> "#B387FC"  // Purple
//            "Kerja" -> "#87CEFA"    // Light blue
//            else -> "#FFFFE0"       // Light yellow for "Semua" or others
//        }
//    }
//
//    companion object {
//        const val REQUEST_CODE_ADD = 1001
//        const val REQUEST_CODE_EDIT = 1002
//    }
//}