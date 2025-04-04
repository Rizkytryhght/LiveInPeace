package com.example.liveinpeace.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.liveinpeace.data.Note
import com.example.liveinpeace.data.repository.NoteRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

//    val allNotes: LiveData<List<Note>> = repository.getAllNotes()

    fun insertNote(note: Note) = viewModelScope.launch {
        repository.insert(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        repository.update(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        repository.delete(note)
    }

    fun getNoteById(id: String) = liveData {
        emit(repository.getNoteById(id))
    }

    fun getAllNotes() = liveData {
        emit(repository.getAllNotes())
    }
}

class NoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

//class NoteViewModel : ViewModel() {
//
//    private val _notes = MutableLiveData<List<Note>>()
//    val notes: LiveData<List<Note>> = _notes
//
//    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
//    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("notes")
//
//    init {
//        loadNotes()
//    }
//
//    fun addNote(note: Note) {
//        val userId = auth.currentUser?.uid ?: return
//        val newNoteId = database.child(userId).push().key ?: return
//        val newNote = note.copy(id = newNoteId)
//        database.child(userId).child(newNoteId).setValue(newNote)
//    }
//
//    fun editNote(note: Note) {
//        val userId = auth.currentUser?.uid ?: return
//        database.child(userId).child(note.id).setValue(note)
//    }
//
//    fun deleteNote(note: Note) {
//        val userId = auth.currentUser?.uid ?: return
//        database.child(userId).child(note.id).removeValue()
//    }
//
//    fun loadNotes() {
//        val userId = auth.currentUser?.uid ?: return
//        database.child(userId).addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val noteList = mutableListOf<Note>()
//                for (noteSnapshot in snapshot.children) {
//                    val note = noteSnapshot.getValue(Note::class.java)
//                    if (note != null) {
//                        noteList.add(note)
//                    }
//                }
//                _notes.value = noteList
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Log error saat Firebase gagal membaca data
//            }
//        })
//    }
//}
