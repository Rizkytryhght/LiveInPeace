package com.example.liveinpeace

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.liveinpeace.ui.auth.AuthActivity
import com.example.liveinpeace.ui.note.NoteActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                // User masih login, langsung ke catatan harian
                startActivity(Intent(this, NoteActivity::class.java))
            } else {
                // Belum login
                startActivity(Intent(this, AuthActivity::class.java))
            }
            finish()
        }, 1500) // Delay 1.5 detik
    }
}