package com.example.liveinpeace.viewModel

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

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = repository.getProfile()
        }
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
            repository.clearProfileData()
            _profileState.value = ProfileModel()
        }
    }
}
