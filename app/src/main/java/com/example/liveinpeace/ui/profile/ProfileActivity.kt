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
            profileViewModel.saveProfileImageUri(it.toString()) // Simpan URI gambar di ViewModel
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        Log.d("DEBUG", "onCreate ProfileActivity berjalan")
        setContentView(binding.root)

        Log.d("ProfileActivity", "onCreate() dipanggil")

        // Tambahkan event listener ke tombol logout dan layout yang membungkusnya
        binding.logoutOption.setOnClickListener {
            Log.d("DEBUG", "Tombol logout diklik!") // Debug tambahan
            performLogout()
        }

        binding.logoutContainer.setOnClickListener {
            performLogout()
        }

        // Observe profile data
        lifecycleScope.launch {
            profileViewModel.profileState.collect { profile ->
                Log.d("ProfileActivity", "Profil diterima: ${profile.firstName} ${profile.lastName}")
                updateProfileUI(profile)
            }
        }

        // Load initial profile data
        profileViewModel.loadProfile()
    }

    override fun onResume() {
        super.onResume()
        Log.d("ProfileActivity", "onResume() dipanggil")
    }

    override fun onStart() {
        super.onStart()
        Log.d("ProfileActivity", "onStart() dipanggil, mencoba memuat ulang profil")

        // Pastikan data profil dimuat ulang setelah login
        profileViewModel.loadProfile()
    }

    private fun performLogout() {
        Log.d("ProfileActivity", "Logout button diklik")
        Toast.makeText(this, "Logout diklik!", Toast.LENGTH_SHORT).show()

        FirebaseAuth.getInstance().signOut()
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.d("ProfileActivity", "User berhasil logout")
        } else {
            Log.e("ProfileActivity", "Logout gagal! User masih ada: ${user.email}")
        }

        val intent = Intent(this@ProfileActivity, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun updateProfileUI(profile: ProfileModel) {
        Log.d("ProfileActivity", "Update UI dengan nama: ${profile.firstName} ${profile.lastName}")

        binding.profileName.text = "${profile.firstName} ${profile.lastName}"

        // Menampilkan gambar profil jika ada
        profile.profileImageUri?.let { uri ->
            binding.profileImage.setImageURI(Uri.parse(uri))
        }
    }
}