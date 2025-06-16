package com.example.liveinpeace.ui.note

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.liveinpeace.data.Note
import com.example.liveinpeace.data.repository.NoteRepository
import com.example.liveinpeace.ui.theme.LiveInPeaceTheme
import com.example.liveinpeace.viewModel.NoteViewModel
import com.example.liveinpeace.viewModel.NoteViewModelFactory

class NoteActivity : AppCompatActivity() {
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup ViewModel
        val repository = NoteRepository()
        val viewModelFactory = NoteViewModelFactory(repository)
        noteViewModel = ViewModelProvider(this, viewModelFactory)[NoteViewModel::class.java]

        // Set Compose content
        setContent {
            LiveInPeaceTheme {
                NotesScreen(noteViewModel = noteViewModel)
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API...")
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
        }
    }

    companion object {
        const val REQUEST_CODE_ADD = 1001
        const val REQUEST_CODE_EDIT = 1002
    }
}