package com.example.liveinpeace

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.liveinpeace.ui.auth.AuthActivity
import com.example.liveinpeace.ui.features.FeaturesListActivity
import com.example.liveinpeace.utils.OnboardingPreferences
import com.example.liveinpeace.ui.onboarding.OnboardingActivity
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
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
                    // Kasus: Sudah login, ke FeaturesListActivity
                    startActivity(Intent(this, FeaturesListActivity::class.java))
                }
            }

            finish()
        }, 1500)
    }
}