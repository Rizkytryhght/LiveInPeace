package com.example.liveinpeace.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.liveinpeace.R
import com.example.liveinpeace.data.ProfileModel
import com.example.liveinpeace.data.repository.ProfileRepository
import com.example.liveinpeace.databinding.ActivityProfileBinding
import com.example.liveinpeace.ui.auth.AuthActivity
import com.example.liveinpeace.ui.note.NoteActivity
import com.example.liveinpeace.viewModel.ProfileViewModel
import com.example.liveinpeace.viewModel.ProfileViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(ProfileRepository(applicationContext))
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.profileImage.setImageURI(it)
            profileViewModel.saveProfileImageUri(it.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("DEBUG", "onCreate ProfileActivity berjalan")

        // Tombol Logout
        binding.logoutOption.setOnClickListener {
            performLogout()
        }
        binding.logoutContainer.setOnClickListener {
            performLogout()
        }

        // Observasi profil
        lifecycleScope.launch {
            profileViewModel.profileState.collect { profile ->
                updateProfileUI(profile)
            }
        }

        profileViewModel.loadProfile()

        // 🚀 Bottom Navigation Setup
        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.selectedItemId = R.id.nav_profile

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_notes -> {
                    startActivity(Intent(this, NoteActivity::class.java))
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
        Log.d("ProfileActivity", "onResume() dipanggil")
    }

    override fun onStart() {
        super.onStart()
        Log.d("ProfileActivity", "onStart() dipanggil")
        profileViewModel.loadProfile()
    }

    private fun performLogout() {
        Toast.makeText(this, "Logout diklik!", Toast.LENGTH_SHORT).show()
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun updateProfileUI(profile: ProfileModel) {
        binding.profileName.text = "${profile.firstName} ${profile.lastName}"
        profile.profileImageUri?.let { uri ->
            binding.profileImage.setImageURI(Uri.parse(uri))
        }
    }
}