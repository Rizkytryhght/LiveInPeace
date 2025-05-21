package com.example.liveinpeace.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moods")
data class Mood(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val moodDescription: String?,
    val timestamp: Long
)