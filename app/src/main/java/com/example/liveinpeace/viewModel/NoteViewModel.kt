package com.example.liveinpeace.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.liveinpeace.data.Note
import com.example.liveinpeace.data.repository.NoteRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _realtimeNotes = MutableLiveData<List<Note>>()
    val realtimeNotes: LiveData<List<Note>> get() = _realtimeNotes

    private var notesListener: ListenerRegistration? = null

    init {
        startListeningToNotes()
    }

    private fun startListeningToNotes() {
        notesListener = repository.listenToNotes { updatedNotes ->
            _realtimeNotes.postValue(updatedNotes)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Hentikan listener saat ViewModel dihancurkan
        notesListener?.remove()
    }

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