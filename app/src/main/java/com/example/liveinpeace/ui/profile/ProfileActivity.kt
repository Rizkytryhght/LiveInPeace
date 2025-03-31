package com.example.liveinpeace.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.liveinpeace.data.ProfileModel
import com.example.liveinpeace.databinding.ActivityProfileBinding
import com.example.liveinpeace.viewModel.ProfileViewModel
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.profileImage.setImageURI(it)
            profileViewModel.saveProfileImageUri(it.toString()) // Simpan URI gambar di ViewModel
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup UI interactions
        setupClickListeners()

        // Observe profile data
        lifecycleScope.launch {
            profileViewModel.profileState.collect { profile ->
                updateProfileUI(profile)
            }
        }

        // Load initial profile data
        profileViewModel.loadProfile()
    }

    private fun setupClickListeners() {
        binding.logoutOption.setOnClickListener {
            profileViewModel.logout()
            finish()
        }

        binding.myAccountOption.setOnClickListener {
            // Navigate to account settings
        }

        binding.helpSupportOption.setOnClickListener {
            // Navigate to help and support screen
        }

        binding.aboutAppOption.setOnClickListener {
            // Navigate to about app screen
        }

        // Tambahkan klik listener untuk mengganti foto profil
        binding.profileImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun updateProfileUI(profile: ProfileModel) {
        binding.profileName.text = "${profile.firstName} ${profile.lastName}"

        // Menampilkan gambar profil jika ada
        profile.profileImageUri?.let { uri ->
            binding.profileImage.setImageURI(Uri.parse(uri))
        }
    }
}
