package com.example.liveinpeace.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.liveinpeace.data.Note
//import com.example.notetakingapp.model.Note
import java.text.SimpleDateFormat
import java.util.Locale

class NoteViewModel : ViewModel() {

    private val _allNotes = mutableListOf(
        Note("1","Meeting Project", "", "10.00 AM", "Mar 20, 2025", "KAMIS", "Semua"),
        Note("2", "Beli Buku Kotlin", "", "14:30 PM", "Fer 20, 2025", "JUMAT","Belajar"),
        Note("3","Workout", "", "07:00 AM", "Apr 20, 2025", "RABU","Semua"),
        Note("4", "Belajar Jetpack Compose", "", "20:00 PM", "Jan 15, 2025", "SENIN", "Belajar")
    )

    private val _filteredNotes = MutableLiveData<List<Note>>()
    val filteredNotes: LiveData<List<Note>> = _filteredNotes

    init {
        _filteredNotes.value = _allNotes
    }

    // Tambah catatan baru
    fun addNote(note: Note) {
        _allNotes.add(note)
        _filteredNotes.value = _allNotes
    }

    // Hapus catatan
    fun deleteNote(index: Int) {
        if (index in _allNotes.indices) {
            _allNotes.removeAt(index)
            _filteredNotes.value = _allNotes
        }
    }

    // Filter catatan berdasarkan pencarian
    fun searchNotes(query: String) {
        _filteredNotes.value = if (query.isEmpty()) {
            _allNotes
        } else {
            _allNotes.filter { it.title.contains(query, ignoreCase = true) }
        }
    }

    // Format tanggal menjadi "Apr 02, 2025 RABU"
    fun formatDateHeader(date: String): String {
        val inputFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormatter = SimpleDateFormat("MMM dd, yyyy EEEE", Locale.getDefault())

        return try {
            val parsedDate = inputFormatter.parse(date)
            outputFormatter.format(parsedDate ?: java.util.Date())
        } catch (e: Exception) {
            date
        }
    }
}
