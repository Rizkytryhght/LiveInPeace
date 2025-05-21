package com.example.liveinpeace.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.liveinpeace.model.MoodEntry
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class MoodRepository {
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw IllegalStateException("User not logged in")

    private val moodCollection = db.collection("users")
        .document(userId)
        .collection("mood_entries")

    suspend fun saveMood(mood: String) {
        try{
        val today = getTodayDate()
        val moodEntry = MoodEntry(mood = mood, timestamp = System.currentTimeMillis())
        moodCollection.document(today).set(moodEntry).await()
    } catch (e: Exception){
        throw Exception("Failed to save mood: ${e.message}")
    }
    }

    suspend fun getAllMoods(): List<MoodEntry> {
        val snapshot = moodCollection.get().await()
        return snapshot.toObjects(MoodEntry::class.java)
    }

    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}