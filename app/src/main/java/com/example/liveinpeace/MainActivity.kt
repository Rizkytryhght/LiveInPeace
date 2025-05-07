package com.example.liveinpeace

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.liveinpeace.ui.auth.AuthActivity
//import com.example.liveinpeace.ui.profile.ProfileActivity
import com.example.liveinpeace.ui.note.NoteActivity
import com.example.liveinpeace.utils.OnboardingPreferences
import com.example.liveinpeace.ui.onboarding.OnboardingActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(Intent(this, AuthActivity::class.java))
        } else {
            startActivity(Intent(this, NoteActivity::class.java))
        }

        finish()
    }
}


//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val user = FirebaseAuth.getInstance().currentUser
//        if (!OnboardingPreferences.isCompleted(this)) {
//            startActivity(Intent(this, OnboardingActivity::class.java))
//            finish()
//            return
//        }
//
//        if (user == null) {
//            // Jika user belum login, pindah ke halaman login
//            startActivity(Intent(this, AuthActivity::class.java))
//            finish()
//        } else {
//            // Jika user sudah login, tetap di halaman utama
//            startActivity(Intent(this, NoteActivity::class.java))
////            startActivity(Intent(this, ProfileActivity::class.java))
//            finish()
//        }
//    }
//}