package com.example.liveinpeace.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {
    private val _userName = MutableStateFlow("User")
    val userName: StateFlow<String> = _userName

    private val _email = MutableStateFlow("user@example.com")
    val email: StateFlow<String> = _email

    fun updateUserName(newName: String) {
        _userName.value = newName
    }

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }
}
