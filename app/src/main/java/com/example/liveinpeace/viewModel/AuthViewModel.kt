package com.example.liveinpeace.viewModel

import androidx.lifecycle.ViewModel
import com.example.liveinpeace.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _otpState = MutableStateFlow<Boolean?>(null)
    val otpState: StateFlow<Boolean?> = _otpState

    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        repository.login(email, password, onComplete)
    }

    fun register(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        repository.register(email, password, onComplete)
    }

    fun sendPasswordReset(email: String, onComplete: (Boolean, String?) -> Unit) {
        repository.sendPasswordReset(email) { success, message ->
            _otpState.value = success
            onComplete(success, message)
        }
    }

    fun changePassword(newPassword: String, onComplete: (Boolean, String?) -> Unit) {
        repository.changePassword(newPassword, onComplete)
    }

    fun resetPassword(email: String, onComplete: (Boolean, String?) -> Unit) {
        repository.resetPassword(email, onComplete)
    }
}
