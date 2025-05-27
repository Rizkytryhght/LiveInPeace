package com.example.liveinpeace.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dass_scores")
data class DASSScore(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val userId: String,
        val depressionScore: Int,
        val anxietyScore: Int,
        val stressScore: Int,
        val timestamp: Long
)