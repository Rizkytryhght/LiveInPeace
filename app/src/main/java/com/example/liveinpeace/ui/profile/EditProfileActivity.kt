package com.example.liveinpeace.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.liveinpeace.data.repository.ProfileRepository
import com.example.liveinpeace.viewModel.ProfileViewModel
import com.example.liveinpeace.viewModel.ProfileViewModelFactory

class EditProfileActivity : ComponentActivity() {
    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(ProfileRepository(applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val profile by viewModel.profileState.collectAsState()

            EditProfileScreen(
                onBackClick = {
                    // Kembali ke ProfileActivity
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                },
                profileViewModel = viewModel
            )
        }

        // Load profile saat activity dimulai
        viewModel.loadProfile()
    }
}