package com.example.liveinpeace.viewModel

import android.net.Uri
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

    private var imageUri: Uri? = null // tambahan internal untuk simpan sementara URI

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            try {
                val profile = repository.getProfile()
                Log.d(tag, "Profil dimuat: $profile")
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
                Log.d(tag, "Profil disinkron dari Firestore: $profile")
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
                Log.d(tag, "Profil diperbarui: $profile")
            } catch (e: Exception) {
                Log.e(tag, "Gagal memperbarui profil: ${e.message}", e)
            }
        }
    }

    fun saveProfileImageUri(uri: String) {
        imageUri = Uri.parse(uri) // Simpan URI secara internal
        viewModelScope.launch {
            try {
                val path = repository.saveProfileImage(imageUri!!)
                if (path.isNotEmpty()) {
                    val updated = _profileState.value.copy(profileImagePath = path)
                    _profileState.value = updated
                    Log.d(tag, "Foto profil diperbarui: $path")
                } else {
                    Log.e(tag, "Gagal menyimpan foto profil")
                }
            } catch (e: Exception) {
                Log.e(tag, "Gagal memproses URI foto: ${e.message}", e)
            }
        }
    }

    fun saveProfile(
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        gender: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val current = _profileState.value
                var localImagePath = current.profileImagePath

                if (imageUri != null) {
                    val savedPath = repository.saveProfileImage(imageUri!!)
                    if (savedPath.isNotEmpty()) {
                        localImagePath = savedPath
                    }
                }

                val updatedProfile = ProfileModel(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    phoneNumber = phoneNumber,
                    gender = gender,
                    profileImagePath = localImagePath
                )

                repository.saveProfile(updatedProfile)
                _profileState.value = updatedProfile
                Log.d(tag, "Profil berhasil disimpan: $updatedProfile")
                onComplete()
            } catch (e: Exception) {
                Log.e(tag, "Gagal menyimpan profil: ${e.message}", e)
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