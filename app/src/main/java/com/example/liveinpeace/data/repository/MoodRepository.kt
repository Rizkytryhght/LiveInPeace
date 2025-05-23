package com.example.liveinpeace.data.repository

import com.example.liveinpeace.model.MoodEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class MoodRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val moodsFlow = MutableStateFlow<List<MoodEntry>>(emptyList())

    init {
        listenToMoods()
    }

    private fun listenToMoods() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(userId)
            .collection("mood_entries")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error listening to moods: ${error.message}")
                    return@addSnapshotListener
                }
                val moodList = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(MoodEntry::class.java)
                    } catch (e: Exception) {
                        println("Error parsing mood entry: ${e.message}")
                        null
                    }
                } ?: emptyList()

                moodsFlow.value = moodList
            }
    }

    fun getAllMoods(): Flow<List<MoodEntry>> {
        return moodsFlow
    }

    suspend fun saveMood(mood: String) {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val today = getTodayDate()
        val moodEntry = MoodEntry(
            mood = mood,
            timestamp = System.currentTimeMillis()
        )
        try {
            firestore.collection("users")
                .document(userId)
                .collection("mood_entries")
                .document(today)
                .set(moodEntry)
                .await()
        } catch (e: Exception) {
            println("Error saving mood: ${e.message}")
            throw Exception("Failed to save mood: ${e.message}")
        }
    }

    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}