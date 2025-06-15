package com.example.liveinpeace.viewModel

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import com.example.liveinpeace.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _otpState = MutableStateFlow<Boolean?>(null)
    val otpState: StateFlow<Boolean?> = _otpState

    // Initialize Google Sign-In
    fun initializeGoogleSignIn(context: Context) {
        repository.initializeGoogleSignIn(context)
    }

    // Get Google Sign-In client
    fun getGoogleSignInClient(): GoogleSignInClient? {
        return repository.getGoogleSignInClient()
    }

    // Sign in with Google
    fun signInWithGoogle(idToken: String, onComplete: (Boolean, String?) -> Unit) {
        repository.signInWithGoogle(idToken, onComplete)
    }

    // Sign out
    fun signOut(context: Context, onComplete: () -> Unit) {
        repository.signOut(context, onComplete)
    }

    // Check if user is logged in
    fun isUserLoggedIn(): Boolean {
        return repository.isUserLoggedIn()
    }

    // Get current user
    fun getCurrentUser(): FirebaseUser? {
        return repository.getCurrentUser()
    }

    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        repository.login(email, password, onComplete)
    }

    fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        gender: String,
        phoneNumber: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        repository.register(email, password, firstName, lastName, gender, phoneNumber, onComplete)
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

    fun sendOtp(phoneNumber: String, activity: Activity, onComplete: (Boolean, String?) -> Unit) {
        repository.sendOtp(phoneNumber, activity, onComplete)
    }

    fun verifyOtp(otp: String, onComplete: (Boolean) -> Unit) {
        repository.verifyOtp(otp, onComplete)
    }
}

