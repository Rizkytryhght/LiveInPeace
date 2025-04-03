package com.example.liveinpeace.data.repository

import com.example.liveinpeace.data.Note
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class NoteRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val notesCollection = firestore.collection("notes")

    // Insert a new note
//    suspend fun insert(note: Note): Boolean {
//        return try {
//            // Set the note document using the note's id
//            notesCollection.document(note.id).set(note).await()
//            true
//        } catch (e: Exception) {
//            e.printStackTrace()
//            false
//        }
//    }

    suspend fun insert(note: Note): Boolean {
        return try {
            if (note.id.isEmpty()) {
                note.id = notesCollection.document().id // Buat ID otomatis jika kosong
            }
            notesCollection.document(note.id).set(note).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Update an existing note
    suspend fun update(note: Note): Boolean {
        return try {
            // Update the note document by its id
            notesCollection.document(note.id).set(note).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Delete a note
    suspend fun delete(note: Note): Boolean {
        return try {
            // Delete the note document by its id
            notesCollection.document(note.id).delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Get a note by its ID
    suspend fun getNoteById(id: String): Note? {
        return try {
            val documentSnapshot = notesCollection.document(id).get().await()
            documentSnapshot.toObject(Note::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Get all notes
    suspend fun getAllNotes(): List<Note> {
        return try {
            val querySnapshot = notesCollection.get().await()
            querySnapshot.toObjects(Note::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

}
