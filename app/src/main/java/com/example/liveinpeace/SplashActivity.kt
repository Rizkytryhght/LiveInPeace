package com.example.liveinpeace

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.liveinpeace.ui.auth.AuthActivity
import com.example.liveinpeace.ui.note.NoteActivity
import com.example.liveinpeace.utils.OnboardingPreferences
import com.example.liveinpeace.ui.onboarding.OnboardingActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val isOnboardingCompleted = OnboardingPreferences.isCompleted(this)
            val user = FirebaseAuth.getInstance().currentUser

            when {
                !isOnboardingCompleted -> {
                    // Kasus: Pertama kali buka aplikasi
                    startActivity(Intent(this, OnboardingActivity::class.java))
                }
                user == null -> {
                    // Kasus: Sudah lihat onboarding, tapi belum login
                    startActivity(Intent(this, AuthActivity::class.java))
                }
                else -> {
                    // Kasus: Sudah login
                    startActivity(Intent(this, NoteActivity::class.java))
                }
            }

            finish()
        }, 1500)
    }
}


//class SplashActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_splash)
//
//        Handler(Looper.getMainLooper()).postDelayed({
//            val user = FirebaseAuth.getInstance().currentUser
//
//            if (!OnboardingPreferences.isCompleted(this)) {
//                startActivity(Intent(this, OnboardingActivity::class.java))
//            } else {
//                val user = FirebaseAuth.getInstance().currentUser
//                if (user != null) {
//                    startActivity(Intent(this, NoteActivity::class.java))
//                } else {
//                    startActivity(Intent(this, AuthActivity::class.java))
//                }
//            }
//
//            finish()
//        }, 1500)
//    }
//}


