package com.example.liveinpeace.viewmodel

import androidx.lifecycle.ViewModel
import com.example.liveinpeace.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        repository.login(email, password, onComplete)
    }

    fun register(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        repository.register(email, password, onComplete)
    }

    fun resetPassword(email: String, onComplete: (Boolean, String?) -> Unit) {
        repository.resetPassword(email, onComplete)
    }
}
