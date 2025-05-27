package com.example.liveinpeace.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DASSScoreDao {
    @Insert
    suspend fun insertScore(score: DASSScore)

    @Query("SELECT * FROM dass_scores WHERE userId = :userId ORDER BY timestamp DESC")
    fun getScoresByUserId(userId: String): Flow<List<DASSScore>>

    @Query("SELECT * FROM dass_scores WHERE userId = :userId AND timestamp >= :startOfMonth AND timestamp < :endOfMonth ORDER BY timestamp DESC")
    fun getScoresForMonth(userId: String, startOfMonth: Long, endOfMonth: Long): Flow<List<DASSScore>>

    @Query("SELECT timestamp FROM dass_scores WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastScoreTimestamp(userId: String): Long?
}