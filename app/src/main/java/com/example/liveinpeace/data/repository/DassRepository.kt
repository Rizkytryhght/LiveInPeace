package com.example.liveinpeace.data.repository

import com.example.liveinpeace.data.local.room.DASSScore
import com.example.liveinpeace.data.local.room.DASSScoreDao
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class DASSRepository(private val dao: DASSScoreDao) {
    suspend fun insertScore(score: DASSScore) {
        dao.insertScore(score)
    }

    fun getScoresByUserId(userId: String): Flow<List<DASSScore>> {
        return dao.getScoresByUserId(userId)
    }

    fun getScoresForCurrentMonth(userId: String): Flow<List<DASSScore>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        val endOfMonth = calendar.timeInMillis

        return dao.getScoresForMonth(userId, startOfMonth, endOfMonth)
    }

    suspend fun getLastScoreTimestamp(userId: String): Long? {
        return dao.getLastScoreTimestamp(userId)
    }
}