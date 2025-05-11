package com.example.liveinpeace.ui.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.liveinpeace.R
import com.example.liveinpeace.data.ProfileModel
import com.example.liveinpeace.data.repository.ProfileRepository
import com.example.liveinpeace.databinding.EditProfileBinding
import com.example.liveinpeace.viewModel.ProfileViewModel
import com.example.liveinpeace.viewModel.ProfileViewModelFactory
import kotlinx.coroutines.launch
import java.io.File

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: EditProfileBinding
    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(ProfileRepository(applicationContext))
    }
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.profileImage.setImageURI(it)
            Log.d("EditProfileActivity", "Foto dipilih: $it")
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            pickImageLauncher.launch("image/*")
        } else {
            Toast.makeText(this, "Izin galeri ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("EditProfileActivity", "onCreate started")

        setupGenderDropdown()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileState.collect { profile ->
                    populateForm(profile)
                }
            }
        }

        binding.updateProfileButton.setOnClickListener {
            try {
                val updatedProfile = collectFormData()
                viewModel.updateProfile(updatedProfile)
                selectedImageUri?.let { uri ->
                    viewModel.saveProfileImageUri(uri.toString())
                }
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                Log.d("EditProfileActivity", "Profil disimpan: $updatedProfile")
                val intent = Intent(this, ProfileActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Log.e("EditProfileActivity", "Gagal menyimpan profil: ${e.message}", e)
                Toast.makeText(this, "Gagal menyimpan profil", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.profileImage.setOnClickListener {
            requestGalleryPermission()
        }
    }

    private fun requestGalleryPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            pickImageLauncher.launch("image/*")
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun setupGenderDropdown() {
        try {
            val genders = listOf("Laki laki", "Perempuan")
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genders)
            binding.genderSpinner.setAdapter(adapter)
        } catch (e: Exception) {
            Log.e("EditProfileActivity", "Error setting up gender dropdown", e)
        }
    }

    private fun populateForm(profile: ProfileModel) {
        try {
            binding.firstNameEditText.setText(profile.firstName)
            binding.lastNameEditText.setText(profile.lastName)
            binding.emailEditText.setText(profile.email)
            if (profile.gender.isNotEmpty()) {
                binding.genderSpinner.setText(profile.gender, false)
            }
            binding.phoneNumberEditText.setText(profile.phoneNumber)
            if (profile.profileImagePath.isNotEmpty()) {
                try {
                    binding.profileImage.setImageURI(Uri.fromFile(File(profile.profileImagePath)))
                    Log.d("EditProfileActivity", "Memuat foto dari: ${profile.profileImagePath}")
                } catch (e: Exception) {
                    Log.e("EditProfileActivity", "Gagal memuat foto: ${e.message}", e)
                    binding.profileImage.setImageResource(R.drawable.user)
                }
            } else {
                binding.profileImage.setImageResource(R.drawable.user)
            }
        } catch (e: Exception) {
            Log.e("EditProfileActivity", "Error in populateForm: ${e.message}", e)
        }
    }

    private fun collectFormData(): ProfileModel {
        val profile = ProfileModel(
            firstName = binding.firstNameEditText.text?.toString() ?: "",
            lastName = binding.lastNameEditText.text?.toString() ?: "",
            email = binding.emailEditText.text?.toString() ?: "",
            gender = binding.genderSpinner.text?.toString() ?: "",
            phoneNumber = binding.phoneNumberEditText.text?.toString() ?: "",
            profileImagePath = viewModel.profileState.value.profileImagePath // Gunakan path lama jika tidak ada foto baru
        )
        Log.d("EditProfileActivity", "Data yang akan disimpan: $profile")
        return profile
    }
}