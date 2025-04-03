package com.example.liveinpeace.data

data class Note(
    var id: String = "", // Ubah dari `val` ke `var`
    val title: String = "",
    val content: String = "",
    val date: String = "",
    val day: String = "",
    val time: String = "",
    val tag: String = "Semua"
)


//data class Note(
//    val id: String,
//    val title: String,
//    val content: String,
//    val date: String,
//    val day: String,
//    val time: String,
//    val tag: String,
//)