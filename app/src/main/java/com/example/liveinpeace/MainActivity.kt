package com.example.liveinpeace

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.liveinpeace.data.Note
import com.example.liveinpeace.data.repository.NoteRepository
import com.example.liveinpeace.ui.auth.AuthActivity
import com.example.liveinpeace.ui.note.NoteAdapter
import com.example.liveinpeace.ui.note.NoteAdapter.OnItemClickListener
import com.example.liveinpeace.ui.note.NoteDetailActivity
import com.example.liveinpeace.viewModel.NoteViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteRepository: NoteRepository
    private lateinit var fabAddNote: FloatingActionButton
    private lateinit var chipSemua: Chip
    private lateinit var chipMotivasi: Chip
    private lateinit var chipBelajar: Chip
    private lateinit var chipKerja: Chip
    private val notesList = ArrayList<Note>()
    private var currentFilter = "Semua"

    // Activity result launcher for NoteDetailActivity
    private val noteDetailLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { data ->
                val id = data.getStringExtra("id") ?: ""
                val title = data.getStringExtra("title") ?: ""
                val content = data.getStringExtra("content") ?: ""
                val tag = data.getStringExtra("tag") ?: "Semua"

                Log.d(TAG, "Catatan baru ditambahkan: $id - $title")

                // Reload catatan setelah memastikan Firestore selesai menyimpan data
                lifecycleScope.launch {
                    val newNote = noteRepository.getNoteById(id)
                    if (newNote != null) {
                        notesList.add(newNote) // Tambah catatan baru ke list
                        noteAdapter.notifyDataSetChanged()
                    } else {
                        Log.e(TAG, "Catatan tidak ditemukan setelah insert")
                    }
                }
            }
        }

//        if (result.resultCode == RESULT_OK) {
//            result.data?.let { data ->
//                // Handle returned data from NoteDetailActivity
//                val id = data.getStringExtra("id") ?: ""
//                val title = data.getStringExtra("title") ?: ""
//                val content = data.getStringExtra("content") ?: ""
//                val date = data.getStringExtra("date") ?: ""
//                val day = data.getStringExtra("day") ?: ""
//                val time = data.getStringExtra("time") ?: ""
//                val tag = data.getStringExtra("tag") ?: "Semua"
//
//                Log.d(TAG, "Note returned from detail: $id, $title")
//
//                // Tunda refresh untuk memberikan waktu Firestore menyimpan data
//                notesRecyclerView.postDelayed({
//                    loadNotes()
//                }, 500)
//            }
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        FirebaseApp.initializeApp(this)

        // Initialize views
        initViews()

        // Initialize ViewModel and Repository
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        noteRepository = NoteRepository()

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            // Jika user belum login, pindah ke halaman login
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        setupRecyclerView()
        setupFilterChips()
        loadNotes() // Memuat catatan dari Repository

        // Setup FAB for adding new notes
        fabAddNote.setOnClickListener {
            // Buka NoteDetailActivity untuk menambah catatan baru
            val intent = Intent(this, NoteDetailActivity::class.java)
            intent.putExtra("is_new", true)
            noteDetailLauncher.launch(intent)
        }
    }

    private fun initViews() {
        notesRecyclerView = findViewById(R.id.notesRecyclerView)
        fabAddNote = findViewById(R.id.addNoteButton)
        chipSemua = findViewById(R.id.chipAll)
        chipMotivasi = findViewById(R.id.chipMotivation)
        chipBelajar = findViewById(R.id.chipStudy)
        chipKerja = findViewById(R.id.chipWork)
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(notesList, object : OnItemClickListener {
            override fun onItemClick(note: Note) {
                // Ketika item diklik, buka halaman detail catatan
                val intent = Intent(this@MainActivity, NoteDetailActivity::class.java)
                intent.putExtra("is_new", false)
                intent.putExtra("note_id", note.id)
                intent.putExtra("title", note.title)
                intent.putExtra("content", note.content)
                intent.putExtra("date", note.date)
                intent.putExtra("day", note.day)
                intent.putExtra("time", note.time)
                intent.putExtra("tag", note.tag)
                noteDetailLauncher.launch(intent)
            }
        },
            { note ->
                deleteNote(note)
            })

        notesRecyclerView.layoutManager = LinearLayoutManager(this)
        notesRecyclerView.adapter = noteAdapter
    }

    private fun setupFilterChips() {
        // Implementasi chips untuk filter catatan berdasarkan tag
        chipSemua.setOnClickListener { filterNotes("Semua") }
        chipMotivasi.setOnClickListener { filterNotes("Motivasi") }
        chipBelajar.setOnClickListener { filterNotes("Belajar") }
        chipKerja.setOnClickListener { filterNotes("Kerja") }
    }

    private fun filterNotes(tag: String) {
        currentFilter = tag
        loadNotes()
    }

    private fun deleteNote(note: Note) {
        // Hapus catatan dari repository menggunakan coroutine
        lifecycleScope.launch {
            try {
                val success = noteRepository.delete(note)
                if (success) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Catatan berhasil dihapus", Toast.LENGTH_SHORT).show()
                    }
                    // Tunda refresh untuk memberikan waktu Firestore menghapus data
                    notesRecyclerView.postDelayed({
                        loadNotes()
                    }, 500)
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Gagal menghapus catatan", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting note", e)
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadNotes() {
        Log.d(TAG, "Loading notes with filter: $currentFilter")

        // Tampilkan loading state
        // (bisa ditambahkan UI loading seperti ProgressBar jika diperlukan)

        // Menggunakan lifecycleScope untuk menjalankan operasi asynchronous
        lifecycleScope.launch {
            try {
                // Ambil semua catatan dari repository
                val allNotes = noteRepository.getAllNotes()
                Log.d(TAG, "Fetched ${allNotes.size} notes from repository")

                // Filter berdasarkan tag jika diperlukan
                val filteredNotes = if (currentFilter == "Semua") {
                    allNotes
                } else {
                    allNotes.filter { it.tag == currentFilter }
                }

                Log.d(TAG, "After filtering: ${filteredNotes.size} notes")

                // Update UI di main thread
                runOnUiThread {
                    updateNotesList(filteredNotes)

                    // Tampilkan pesan jika tidak ada catatan
                    if (filteredNotes.isEmpty()) {
                        Toast.makeText(this@MainActivity,
                            if (currentFilter == "Semua") "Belum ada catatan"
                            else "Tidak ada catatan dengan tag $currentFilter",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading notes", e)
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Gagal memuat catatan: ${e.message}", Toast.LENGTH_SHORT).show()
                    // Fallback ke data sampel jika terjadi kesalahan
                    loadSampleNotes()
                }
            }
        }
    }

    private fun loadSampleNotes() {
        // Sample data sesuai dengan mockup
        val notes = listOf(
            Note(
                id = "1",
                title = "My Journal",
                content = "",
                time = "11:00 AM",
                date = "Mar 20, 2025",
                day = "KAMIS",
                tag = "Semua"
            ),
            Note(
                id = "2",
                title = "Tugas Hari Ini",
                content = "",
                time = "12:30 PM",
                date = "Apr 02, 2024",
                day = "RABU",
                tag = "Belajar"
            )
        )

        updateNotesList(notes)
    }

    private fun updateNotesList(notes: List<Note>) {
        notesList.clear()
        notesList.addAll(notes)
        noteAdapter.notifyDataSetChanged()

        Log.d(TAG, "Updated notes list: ${notesList.size} items")
    }

    override fun onResume() {
        super.onResume()
        // Muat ulang catatan setiap kali activity menjadi aktif kembali
        loadNotes()
    }
}

//import android.content.Intent
//import android.os.Bundle
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.liveinpeace.data.Note
//import com.example.liveinpeace.data.repository.NoteRepository
//import com.example.liveinpeace.ui.auth.AuthActivity
//import com.example.liveinpeace.ui.note.NoteAdapter
//import com.example.liveinpeace.ui.note.NoteAdapter.OnItemClickListener
//import com.example.liveinpeace.ui.note.NoteDetailActivity
//import com.example.liveinpeace.viewModel.NoteViewModel
//import com.google.android.material.chip.Chip
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.FirebaseApp
//import kotlinx.coroutines.launch
//
//class MainActivity : AppCompatActivity() {
//    private lateinit var notesRecyclerView: RecyclerView
//    private lateinit var noteAdapter: NoteAdapter
//    private lateinit var noteViewModel: NoteViewModel
//    private lateinit var noteRepository: NoteRepository
//    private lateinit var fabAddNote: FloatingActionButton
//    private lateinit var chipSemua: Chip
//    private lateinit var chipMotivasi: Chip
//    private lateinit var chipBelajar: Chip
//    private lateinit var chipKerja: Chip
//    private val notesList = ArrayList<Note>()
//    private var currentFilter = "Semua"
//
//    // Activity result launcher for NoteDetailActivity
//    private val noteDetailLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == RESULT_OK) {
//            result.data?.let { data ->
//                // Handle returned data from NoteDetailActivity
//                val id = data.getStringExtra("id") ?: ""
//                val title = data.getStringExtra("title") ?: ""
//                val content = data.getStringExtra("content") ?: ""
//                val date = data.getStringExtra("date") ?: ""
//                val day = data.getStringExtra("day") ?: ""
//                val time = data.getStringExtra("time") ?: ""
//                val tag = data.getStringExtra("tag") ?: "Semua"
//
//                // Refresh list after note update
//                loadNotes()
//            }
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_note)
//        FirebaseApp.initializeApp(this)
//
//        // Initialize views
//        initViews()
//
//        // Initialize ViewModel and Repository
//        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
//        noteRepository = NoteRepository()
//
//        val user = FirebaseAuth.getInstance().currentUser
//        if (user == null) {
//            // Jika user belum login, pindah ke halaman login
//            startActivity(Intent(this, AuthActivity::class.java))
//            finish()
//            return
//        }
//
//        setupRecyclerView()
//        setupFilterChips()
//        loadNotes() // Memuat catatan dari Repository
//
//        // Setup FAB for adding new notes
//        fabAddNote.setOnClickListener {
//            // Buka NoteDetailActivity untuk menambah catatan baru
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
//        noteAdapter = NoteAdapter(notesList, object : OnItemClickListener {
//            override fun onItemClick(note: Note) {
//                // Ketika item diklik, buka halaman detail catatan
//                val intent = Intent(this@MainActivity, NoteDetailActivity::class.java)
//                intent.putExtra("is_new", false)
//                intent.putExtra("note_id", note.id)
//                intent.putExtra("title", note.title)
//                intent.putExtra("content", note.content)
//                intent.putExtra("date", note.date)
//                intent.putExtra("day", note.day)
//                intent.putExtra("time", note.time)
//                intent.putExtra("tag", note.tag)
//                noteDetailLauncher.launch(intent)
//            }
//        },
//            { note ->
//                deleteNote(note)
//            })
//
//        notesRecyclerView.layoutManager = LinearLayoutManager(this)
//        notesRecyclerView.adapter = noteAdapter
//    }
//
//    private fun setupFilterChips() {
//        // Implementasi chips untuk filter catatan berdasarkan tag
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
//    private fun deleteNote(note: Note) {
//        // Hapus catatan dari repository menggunakan coroutine
//        lifecycleScope.launch {
//            val success = noteRepository.delete(note)
//            if (success) {
//                loadNotes() // Muat ulang daftar setelah penghapusan
//            }
//        }
//    }
//
//    private fun loadNotes() {
//        // Menggunakan lifecycleScope untuk menjalankan operasi asynchronous
//        lifecycleScope.launch {
//            try {
//                // Ambil semua catatan dari repository
//                val allNotes = noteRepository.getAllNotes()
//
//                // Filter berdasarkan tag jika diperlukan
//                val filteredNotes = if (currentFilter == "Semua") {
//                    allNotes
//                } else {
//                    allNotes.filter { it.tag == currentFilter }
//                }
//
//                // Update UI di main thread
//                updateNotesList(filteredNotes)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                // Fallback ke data sampel jika terjadi kesalahan
//                loadSampleNotes()
//            }
//        }
//    }
//
//    private fun loadSampleNotes() {
//        // Sample data sesuai dengan mockup
//        val notes = listOf(
//            Note(
//                id = "1",
//                title = "My Journal",
//                content = "",
//                time = "11:00 AM",
//                date = "Mar 20, 2025",
//                day = "KAMIS",
//                tag = "Semua"
//            ),
//            Note(
//                id = "2",
//                title = "Tugas Hari Ini",
//                content = "",
//                time = "12:30 PM",
//                date = "Apr 02, 2024",
//                day = "RABU",
//                tag = "Belajar"
//            )
//        )
//
//        updateNotesList(notes)
//    }
//
//    private fun updateNotesList(notes: List<Note>) {
//        notesList.clear()
//        notesList.addAll(notes)
//        noteAdapter.notifyDataSetChanged()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        // Muat ulang catatan setiap kali activity menjadi aktif kembali
//        loadNotes()
//    }
//}
//
//


//package com.example.liveinpeace
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.example.liveinpeace.ui.auth.AuthActivity
//import com.example.liveinpeace.ui.profile.ProfileActivity
//import com.google.firebase.auth.FirebaseAuth
//
//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val user = FirebaseAuth.getInstance().currentUser
//        if (user == null) {
//            // Jika user belum login, pindah ke halaman login
//            startActivity(Intent(this, AuthActivity::class.java))
//            finish()
//        } else {
//            // Jika user sudah login, tetap di halaman utama
//            startActivity(Intent(this, ProfileActivity::class.java))
//            finish()
//        }
//    }
//}