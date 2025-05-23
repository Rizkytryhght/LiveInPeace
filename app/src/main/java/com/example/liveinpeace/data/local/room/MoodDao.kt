package com.example.liveinpeace.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MoodDao {
    @Insert
    suspend fun insert(mood: Mood)

    @Query("SELECT * FROM moods WHERE userId = :userId")
    suspend fun getMoodsByUser(userId: String): List<Mood>
}