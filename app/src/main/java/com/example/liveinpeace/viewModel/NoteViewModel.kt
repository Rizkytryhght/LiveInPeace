package com.example.liveinpeace.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.liveinpeace.data.Note
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Locale

class NoteViewModel : ViewModel() {

    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("notes")

    fun addNote(note: Note) {
        val newNoteId = database.push().key ?: return // Generate unique ID
        val newNote = note.copy(id = newNoteId) // Assign new ID
        database.child(newNoteId).setValue(newNote)
    }

    // Mengedit catatan di Firebase
    fun editNote(note: Note) {
        database.child(note.id).setValue(note)
    }

    // Menghapus catatan dari Firebase
    fun deleteNote(note: Note) {
        database.child(note.id).removeValue()
    }

    // Memuat catatan dari Firebase
    fun loadNotes() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val noteList = mutableListOf<Note>()
                for (noteSnapshot in snapshot.children) {
                    val note = noteSnapshot.getValue(Note::class.java)
                    if (note != null) {
                        noteList.add(note)
                    }
                }
                _notes.value = noteList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}