package com.example.liveinpeace

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.liveinpeace.databinding.ActivityMainBinding
import com.example.liveinpeace.data.Note
import com.example.liveinpeace.ui.auth.AuthActivity
import com.example.liveinpeace.ui.note.NoteAdapter
import com.example.liveinpeace.ui.note.NoteAdapter.OnItemClickListener
import com.example.liveinpeace.ui.note.NoteDetailActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp

//class MyApplication : Application() {
//    override fun onCreate() {
//        super.onCreate()
//        FirebaseApp.initializeApp(this)
//    }
//}


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var noteAdapter: NoteAdapter
    private val notesList = ArrayList<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            // Jika user belum login, pindah ke halaman login
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        setupRecyclerView()
        loadSampleNotes() // Untuk demo, nantinya diganti dengan data dari database

        // Setup FAB for adding new notes
        val fab: FloatingActionButton = binding.fabAddNote
        fab.setOnClickListener {
            // TODO: Implementasi tambah catatan baru
            // startActivity(Intent(this, AddNoteActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(notesList, object : OnItemClickListener {
            override fun onItemClick(note: Note) {
                // Ketika item diklik, bisa pindah ke halaman detail catatan
                val intent = Intent(this@MainActivity, NoteDetailActivity::class.java)
                intent.putExtra("note_id", note.id)
                startActivity(intent)
            }
        },
            { note ->
                deleteNote(note)
            })
        binding.notesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = noteAdapter
        }
    }

    private fun deleteNote(note: Note) {
        // Hapus catatan dari daftar
        val position = notesList.indexOf(note)
        if (position != -1) {
            notesList.removeAt(position)
            noteAdapter.notifyItemRemoved(position)
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

        notesList.clear()
        notesList.addAll(notes)
        noteAdapter.notifyDataSetChanged()
    }
}




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