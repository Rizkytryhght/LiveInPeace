package com.example.liveinpeace.data

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Note(
    var id: String = "",
    val title: String = "",
    val content: String = "",
    val date: String = "",
    val time: String = "",
    val tag: String = "Semua"
)