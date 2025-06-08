package com.example.liveinpeace.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityOptionsCompat
import com.example.liveinpeace.data.repository.ProfileRepository
import com.example.liveinpeace.ui.auth.AuthActivity
import com.example.liveinpeace.ui.features.FeaturesListActivity
import com.example.liveinpeace.ui.note.NoteActivity
import com.example.liveinpeace.viewModel.ProfileViewModel
import com.example.liveinpeace.viewModel.ProfileViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : ComponentActivity() {
    private val profileViewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(ProfileRepository(applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val profile by profileViewModel.profileState.collectAsState()

            ProfileScreen(
                profile = profile,
                onEditClick = {
                    startActivity(Intent(this, EditProfileActivity::class.java))
                },
                onLogoutClick = {
                    FirebaseAuth.getInstance().signOut()
                    profileViewModel.logout()
                    startActivity(Intent(this, AuthActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                },
                onNavigate = { route ->
                    val intent = when (route) {
                        "notes" -> Intent(this, NoteActivity::class.java)
                        "features" -> Intent(this, FeaturesListActivity::class.java)
                        "profile" -> null
                        else -> null
                    }

                    intent?.let {
                        val options = ActivityOptionsCompat.makeCustomAnimation(this, 0, 0)
                        startActivity(it, options.toBundle())
                        finish()
                    }
                },
                currentRoute = "profile"
            )
        }

        profileViewModel.loadProfile()
    }

    override fun onResume() {
        super.onResume()
        profileViewModel.loadProfile()
    }
}