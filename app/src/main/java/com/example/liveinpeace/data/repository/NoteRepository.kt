package com.example.liveinpeace.data.repository

import android.util.Log
import com.example.liveinpeace.data.Note
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class NoteRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val notesCollection = firestore.collection("notes")

    suspend fun insert(note: Note): Boolean {
        return try {
            if (note.id.isEmpty()) {
                note.id = notesCollection.document().id
            }
            notesCollection.document(note.id).set(note).await()
            Log.d("NoteRepository", "Note inserted: ${note.id}")
            true
        } catch (e: Exception) {
            Log.e("NoteRepository", "Insert failed", e)
            false
        }
    }

    suspend fun update(note: Note): Boolean {
        return try {
            notesCollection.document(note.id).set(note).await()
            Log.d("NoteRepository", "Note updated: ${note.id}")
            true
        } catch (e: Exception) {
            Log.e("NoteRepository", "Update failed", e)
            false
        }
    }

    suspend fun delete(note: Note): Boolean {
        return try {
            notesCollection.document(note.id).delete().await()
            Log.d("NoteRepository", "Note deleted: ${note.id}")
            true
        } catch (e: Exception) {
            Log.e("NoteRepository", "Delete failed", e)
            false
        }
    }

    fun getNoteById(noteId: String): Task<DocumentSnapshot> {
        return notesCollection.document(noteId).get()
    }

    suspend fun getAllNotes(): List<Note> {
        return try {
            val querySnapshot = notesCollection.get().await()
            val notes = querySnapshot.toObjects(Note::class.java)
            Log.d("NoteRepository", "Total notes fetched: ${notes.size}")
            notes
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // ✅ Tambahan: Listener untuk realtime update
    fun listenToNotes(onDataChanged: (List<Note>) -> Unit): ListenerRegistration {
        return notesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                error.printStackTrace()
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val notes = snapshot.toObjects(Note::class.java)
                Log.d("NoteRepository", "Realtime update: ${notes.size} notes")
                onDataChanged(notes)
            } else {
                onDataChanged(emptyList())
            }
        }
    }
}