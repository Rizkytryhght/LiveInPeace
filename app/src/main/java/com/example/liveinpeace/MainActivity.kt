package com.example.liveinpeace

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.liveinpeace.ui.auth.AuthActivity
import com.example.liveinpeace.ui.profile.ProfileActivity
import com.example.liveinpeace.ui.note.NoteActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            // Jika user belum login, pindah ke halaman login
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        } else {
            // Jika user sudah login, tetap di halaman utama
            startActivity(Intent(this, NoteActivity::class.java))
//            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }
}

//import android.content.Intent
//import android.os.Bundle
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.liveinpeace.data.Note
//import com.example.liveinpeace.ui.auth.AuthActivity
//import com.example.liveinpeace.ui.note.NoteAdapter
//import com.example.liveinpeace.ui.note.NoteDetailActivity
//import com.example.liveinpeace.viewModel.NoteViewModel
//import com.google.android.material.chip.Chip
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import com.google.firebase.auth.FirebaseAuth
//import kotlinx.coroutines.launch
//
//class MainActivity : AppCompatActivity() {
//    private lateinit var notesRecyclerView: RecyclerView
//    private lateinit var noteAdapter: NoteAdapter
//    private lateinit var noteViewModel: NoteViewModel
//    private lateinit var fabAddNote: FloatingActionButton
//    private lateinit var chipSemua: Chip
//    private lateinit var chipMotivasi: Chip
//    private lateinit var chipBelajar: Chip
//    private lateinit var chipKerja: Chip
//    private val notesList = ArrayList<Note>()
//    private var currentFilter = "Semua"
//
//    private val noteDetailLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == RESULT_OK) {
//            loadNotes()
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_note)
//
//        if (FirebaseAuth.getInstance().currentUser == null) {
//            startActivity(Intent(this, AuthActivity::class.java))
//            finish()
//            return
//        }
//
//        initViews()
//        setupRecyclerView()
//        setupFilterChips()
//
//        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
//
//        noteViewModel.notes.observe(this) { notes ->
//            updateNotesList(notes)
//        }
//
//        fabAddNote.setOnClickListener {
//            val intent = Intent(this, NoteDetailActivity::class.java)
//            intent.putExtra("is_new", true)
//            noteDetailLauncher.launch(intent)
//        }
//    }
//
//    private fun initViews() {
//        notesRecyclerView = findViewById(R.id.notesRecyclerView)
//        fabAddNote = findViewById(R.id.addNoteButton)
//        chipSemua = findViewById(R.id.chipAll)
//        chipMotivasi = findViewById(R.id.chipMotivation)
//        chipBelajar = findViewById(R.id.chipStudy)
//        chipKerja = findViewById(R.id.chipWork)
//    }
//
//    private fun setupRecyclerView() {
//        noteAdapter = NoteAdapter(notesList,
//            OnItemClickListener = { note ->
//                val intent = Intent(this, NoteDetailActivity::class.java).apply {
//                    putExtra("is_new", false)
//                    putExtra("note_id", note.id)
//                    putExtra("title", note.title)
//                    putExtra("content", note.content)
//                    putExtra("tag", note.tag)
//                }
//                noteDetailLauncher.launch(intent)
//            },
//            onDeleteClick = { note ->
//                noteViewModel.deleteNote(note)
//            })
//
//        notesRecyclerView.layoutManager = LinearLayoutManager(this)
//        notesRecyclerView.adapter = noteAdapter
//    }
//
//    private fun setupFilterChips() {
//        chipSemua.setOnClickListener { filterNotes("Semua") }
//        chipMotivasi.setOnClickListener { filterNotes("Motivasi") }
//        chipBelajar.setOnClickListener { filterNotes("Belajar") }
//        chipKerja.setOnClickListener { filterNotes("Kerja") }
//    }
//
//    private fun filterNotes(tag: String) {
//        currentFilter = tag
//        loadNotes()
//    }
//
//    private fun loadNotes() {
//        noteViewModel.notes.observe(this) { allNotes ->
//            val filteredNotes = if (currentFilter == "Semua") {
//                allNotes
//            } else {
//                allNotes.filter { it.tag == currentFilter }
//            }
//            updateNotesList(filteredNotes)
//        }
//    }
//
//    private fun updateNotesList(notes: List<Note>) {
//        notesList.clear()
//        notesList.addAll(notes)
//        noteAdapter.notifyDataSetChanged()
//
//        if (notes.isEmpty()) {
//            Toast.makeText(this, "Tidak ada catatan dengan tag $currentFilter", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        loadNotes()
//    }
//}

