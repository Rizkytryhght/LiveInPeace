package com.example.liveinpeace.data.repository

import com.example.liveinpeace.data.local.room.DASSScore
import com.example.liveinpeace.data.local.room.DASSScoreDao
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar

class DASSRepository(
    private val dao: DASSScoreDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /**
     * Menyimpan skor DASS ke Firestore kalau ada koneksi internet
     * Tidak menyimpan ke Room atau Firestore kalau offline
     * Mengembalikan true kalau sukses, false kalau gagal (misal, offline)
     */
    suspend fun insertScore(score: DASSScore, isOnline: Boolean): Boolean {
        return withContext(Dispatchers.IO) {
            if (!isOnline) {
                println("No internet connection, skipping save")
                return@withContext false
            }

            try {
                firestore.collection("users")
                    .document(score.userId)
                    .collection("dass_scores")
                    .document(score.timestamp.toString())
                    .set(score)
                    .await()
                println("Successfully saved to Firestore: ${score.timestamp}")
                true
            } catch (e: Exception) {
                println("Error saving to Firestore: ${e.message}")
                false
            }
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
     * Mengambil timestamp terakhir user mengisi DASS dari Room dan Firestore
     * Mengembalikan timestamp terbaru atau null kalau belum ada data
     */
    suspend fun getLastScoreTimestamp(userId: String): Long? {
        return withContext(Dispatchers.IO) {
            // Cek Room
            val roomTimestamp = dao.getLastScoreTimestamp(userId)

            // Cek Firestore
            val firestoreTimestamp = try {
                firestore.collection("users")
                    .document(userId)
                    .collection("dass_scores")
                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .await()
                    .documents
                    .firstOrNull()
                    ?.getLong("timestamp")
            } catch (e: Exception) {
                println("Error fetching Firestore timestamp: ${e.message}")
                null
            }

            // Ambil timestamp terbaru dari Room atau Firestore
            listOfNotNull(roomTimestamp, firestoreTimestamp).maxOrNull()
        }
    }

    /**
     * Menghapus semua skor DASS dari Room
     */
    suspend fun clearAllScores() {
        withContext(Dispatchers.IO) {
            dao.deleteAllScores()
        }
    }

    /**
     * Mengambil semua skor dari Firestore dan Room untuk user tertentu
     * Mengembalikan daftar skor unik, diurutkan dari terbaru
     */
    suspend fun getAllScores(userId: String): List<DASSScore> {
        return withContext(Dispatchers.IO) {
            // Log userId
            println("Fetching scores for userId: $userId")

            // Ambil dari Room
            val roomScores = try {
                val data = dao.getScoresByUserId(userId).firstOrNull() ?: emptyList()
                println("Room scores fetched: ${data.size}")
                data
            } catch (e: Exception) {
                println("Error fetching scores from Room: ${e.message}")
                emptyList()
            }

            // Ambil dari Firestore
            val firestoreScores = try {
                val snapshot = firestore.collection("users")
                    .document(userId)
                    .collection("dass_scores")
                    .get()
                    .await()
                val scores = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(DASSScore::class.java)?.copy(id = doc.getLong("id")?.toInt() ?: 0)
                    } catch (e: Exception) {
                        println("Error parsing Firestore document ${doc.id}: ${e.message}")
                        null
                    }
                }
                println("Firestore scores fetched: ${scores.size}")
                scores.forEach { println("Firestore score: $it") }
                scores
            } catch (e: Exception) {
                println("Error fetching Firestore scores: ${e.message}")
                emptyList()
            }

            // Gabungkan, hapus duplikat berdasarkan timestamp, urut terbaru
            val combinedScores = (roomScores + firestoreScores)
                .distinctBy { it.timestamp }
                .sortedByDescending { it.timestamp }
            println("Total combined scores: ${combinedScores.size}")
            combinedScores
        }
    }
}