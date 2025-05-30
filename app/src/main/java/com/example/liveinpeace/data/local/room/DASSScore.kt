package com.example.liveinpeace.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity(tableName = "dass_scores")
data class DASSScore(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val userId: String = "",
        val depressionScore: Int = 0,
        val anxietyScore: Int = 0,
        val stressScore: Int = 0,
        val timestamp: Long = 0L
)