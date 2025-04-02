package com.example.liveinpeace.viewModel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveinpeace.LiveInPeaceApp
import com.example.liveinpeace.data.ProfileModel
import com.example.liveinpeace.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {
    private val _profileState = MutableStateFlow(ProfileModel())
    val profileState: StateFlow<ProfileModel> = _profileState.asStateFlow()

    private val sharedPreferences = LiveInPeaceApp.instance.getSharedPreferences("user_prefs", Activity.MODE_PRIVATE)

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            val profile = repository.getProfile()
            Log.d("ProfileViewModel", "Data dari SharedPreferences: ${profile.firstName} ${profile.lastName}")

            _profileState.value = profile
        }
    }

    fun refreshProfileAfterLogin() {
        Log.d("ProfileViewModel", "Memuat ulang profil setelah login...")
        loadProfile() // Paksa muat ulang data setelah login
    }

    fun updateProfile(profile: ProfileModel) {
        viewModelScope.launch {
            repository.saveProfile(profile)
            _profileState.value = profile
        }
    }

    fun saveProfileImageUri(uri: String) {
        viewModelScope.launch {
            repository.saveProfileImage(uri)
            _profileState.value = _profileState.value.copy(profileImageUri = uri)
        }
    }

    fun logout() {
        viewModelScope.launch {
            sharedPreferences.edit().clear().apply()
            _profileState.value = ProfileModel() // Reset state profil
            Log.d("ProfileViewModel", "Logout berhasil, profil di-reset.")
        }
    }
}