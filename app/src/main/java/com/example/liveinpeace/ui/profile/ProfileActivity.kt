package com.example.liveinpeace.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.liveinpeace.R
import com.example.liveinpeace.data.ProfileModel
import com.example.liveinpeace.data.repository.ProfileRepository
import com.example.liveinpeace.databinding.ActivityProfileBinding
import com.example.liveinpeace.ui.auth.AuthActivity
import com.example.liveinpeace.ui.features.FeaturesListActivity
import com.example.liveinpeace.ui.note.NoteActivity
import com.example.liveinpeace.viewModel.ProfileViewModel
import com.example.liveinpeace.viewModel.ProfileViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.io.File

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(ProfileRepository(applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logoutOption.setOnClickListener {
            performLogout()
        }
        binding.logoutContainer.setOnClickListener {
            performLogout()
        }

        binding.editProfileIcon.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        lifecycleScope.launch {
            profileViewModel.profileState.collect { profile ->
                updateProfileUI(profile)
            }
        }

        profileViewModel.loadProfile()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_profile
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_notes -> {
                    startActivity(Intent(this, NoteActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_features -> {
                    startActivity(Intent(this, FeaturesListActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("ProfileActivity", "onResume: Memuat ulang profil")
        profileViewModel.loadProfile()
    }

    private fun performLogout() {
        Toast.makeText(this, "Logout diklik!", Toast.LENGTH_SHORT).show()
        FirebaseAuth.getInstance().signOut()
        profileViewModel.logout()
        startActivity(Intent(this, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun updateProfileUI(profile: ProfileModel) {
        binding.profileName.text = "${profile.firstName} ${profile.lastName}"
        binding.profileUsername.text = profile.email
        if (profile.profileImagePath.isNotBlank()) {
            try {
                binding.profileImage.setImageURI(Uri.fromFile(File(profile.profileImagePath)))
                Log.d("ProfileActivity", "Memuat foto di ProfileActivity: ${profile.profileImagePath}")
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Gagal memuat foto: ${e.message}", e)
                binding.profileImage.setImageResource(R.drawable.user)
            }
        } else {
            binding.profileImage.setImageResource(R.drawable.user)
        }
    }
}