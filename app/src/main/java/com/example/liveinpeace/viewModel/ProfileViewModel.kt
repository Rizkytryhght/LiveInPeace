package com.example.liveinpeace.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveinpeace.data.ProfileModel
import com.example.liveinpeace.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {
    private val _profileState = MutableStateFlow(ProfileModel())
    val profileState: StateFlow<ProfileModel> = _profileState.asStateFlow()
    private val tag = "ProfileViewModel"

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            try {
                val profile = repository.getProfile()
                Log.d(tag, "Profil dimuat: ${profile.firstName} ${profile.lastName} ${profile.email} ${profile.gender} ${profile.phoneNumber} ${profile.profileImagePath}")
                _profileState.value = profile
            } catch (e: Exception) {
                Log.e(tag, "Gagal memuat profil: ${e.message}", e)
            }
        }
    }

    fun refreshProfileAfterLogin() {
        Log.d(tag, "Memuat ulang profil setelah login...")
        viewModelScope.launch {
            try {
                val profile = repository.syncProfileFromFirestore()
                Log.d(tag, "Profil disinkron dari Firestore: ${profile.firstName} ${profile.lastName}")
                _profileState.value = profile
            } catch (e: Exception) {
                Log.e(tag, "Gagal sinkron profil setelah login: ${e.message}", e)
            }
        }
    }

    fun updateProfile(profile: ProfileModel) {
        viewModelScope.launch {
            try {
                repository.saveProfile(profile)
                _profileState.value = profile
                Log.d(tag, "Profil diperbarui: ${profile.firstName} ${profile.lastName}")
            } catch (e: Exception) {
                Log.e(tag, "Gagal memperbarui profil: ${e.message}", e)
            }
        }
    }

    fun saveProfileImageUri(uri: String) {
        viewModelScope.launch {
            try {
                val path = repository.saveProfileImage(android.net.Uri.parse(uri))
                if (path.isNotEmpty()) {
                    _profileState.value = _profileState.value.copy(profileImagePath = path)
                    Log.d(tag, "Foto profil diperbarui: $path")
                } else {
                    Log.e(tag, "Gagal menyimpan foto profil")
                }
            } catch (e: Exception) {
                Log.e(tag, "Gagal memproses URI foto: ${e.message}", e)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                repository.clearProfileData()
                _profileState.value = ProfileModel()
                Log.d(tag, "Logout berhasil, profil dan Room DB di-reset.")
            } catch (e: Exception) {
                Log.e(tag, "Gagal logout: ${e.message}", e)
            }
        }
    }
}