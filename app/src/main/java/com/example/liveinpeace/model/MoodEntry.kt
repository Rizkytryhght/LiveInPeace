package com.example.liveinpeace.model

import androidx.annotation.Keep

@Keep
data class MoodEntry(
    val mood: String = "",
    val timestamp: Long = 0L
)