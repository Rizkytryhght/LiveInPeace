package com.example.liveinpeace.data.repository

import com.example.liveinpeace.data.local.room.DASSScore
import com.example.liveinpeace.data.local.room.DASSScoreDao
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class DASSRepository(
    private val dao: DASSScoreDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /**
     * Menyimpan skor DASS ke Room dan Firestore
     */
    suspend fun insertScore(score: DASSScore) {
        // Simpan ke Room Database lokal
        dao.insertScore(score)

        // Simpan ke Firebase Firestore (cloud)
        try {
            firestore.collection("users")
                .document(score.userId)
                .collection("dass_scores")
                .document(score.timestamp.toString())
                .set(score)
                .await()
            println("Successfully saved to Firestore: ${score.timestamp}")
        } catch (e: Exception) {
            println("Error saving to Firestore: ${e.message}")
        }
    }

    /**
     * Mengambil seluruh skor milik user tertentu dari Room
     */
    fun getScoresByUserId(userId: String): Flow<List<DASSScore>> {
        return dao.getScoresByUserId(userId)
    }

    /**
     * Mengambil seluruh skor milik user untuk bulan ini dari Room
     */
    fun getScoresForCurrentMonth(userId: String): Flow<List<DASSScore>> {
        val calendar = Calendar.getInstance()

        // Atur ke awal bulan (00:00 tanggal 1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis

        // Atur ke awal bulan berikutnya
        calendar.add(Calendar.MONTH, 1)
        val endOfMonth = calendar.timeInMillis

        return dao.getScoresForMonth(userId, startOfMonth, endOfMonth)
    }

    /**
     * Mengambil timestamp terakhir user mengisi DASS
     */
    suspend fun getLastScoreTimestamp(userId: String): Long? {
        return dao.getLastScoreTimestamp(userId)
    }
}