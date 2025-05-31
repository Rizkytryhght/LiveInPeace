package com.example.liveinpeace.data.repository

import com.example.liveinpeace.model.MoodEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MoodRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun saveMood(mood: String): Boolean {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val today = getTodayDate()
        val moodEntry = MoodEntry(
            mood = mood,
            timestamp = System.currentTimeMillis()
        )
        return try {
            withContext(Dispatchers.IO) {
                firestore.collection("users")
                    .document(userId)
                    .collection("mood_entries")
                    .document(today)
                    .set(moodEntry)
                    .await()
                println("Mood saved: $mood on $today")
                true
            }
        } catch (e: Exception) {
            println("Error saving mood: ${e.message}")
            false
        }
    }

    suspend fun getMoodsLast7Days(): List<MoodEntry> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val startTime = calendar.timeInMillis

        return try {
            withContext(Dispatchers.IO) {
                val snapshot = firestore.collection("users")
                    .document(userId)
                    .collection("mood_entries")
                    .whereGreaterThanOrEqualTo("timestamp", startTime)
                    .whereLessThanOrEqualTo("timestamp", endTime)
                    .get()
                    .await()
                val moods = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(MoodEntry::class.java)
                    } catch (e: Exception) {
                        println("Error parsing mood entry: ${e.message}")
                        null
                    }
                }
                println("Fetched ${moods.size} moods for last 7 days")
                moods.sortedBy { it.timestamp }
            }
        } catch (e: Exception) {
            println("Error fetching moods: ${e.message}")
            emptyList()
        }
    }

    suspend fun hasSubmittedMoodToday(): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        val today = getTodayDate()
        return try {
            withContext(Dispatchers.IO) {
                val snapshot = firestore.collection("users")
                    .document(userId)
                    .collection("mood_entries")
                    .document(today)
                    .get()
                    .await()
                snapshot.exists()
            }
        } catch (e: Exception) {
            println("Error checking today's mood: ${e.message}")
            false
        }
    }

    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}