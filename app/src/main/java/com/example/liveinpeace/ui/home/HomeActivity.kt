package com.example.liveinpeace.ui.home
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.example.liveinpeace.databinding.ActivityHomeBinding
//import com.example.liveinpeace.ui.auth.AuthActivity
//import com.google.firebase.auth.FirebaseAuth
//
//class HomeActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityHomeBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityHomeBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.btnLogout.setOnClickListener {
//            FirebaseAuth.getInstance().signOut()
//            startActivity(Intent(this, AuthActivity::class.java))
//            finish()
//        }
//    }
//}