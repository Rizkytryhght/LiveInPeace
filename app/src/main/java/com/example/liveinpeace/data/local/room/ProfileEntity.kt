package com.example.liveinpeace.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = 1,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val gender: String,
    val profileImagePath: String
)