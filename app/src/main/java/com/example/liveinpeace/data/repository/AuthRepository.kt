package com.example.liveinpeace.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun register(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun sendPasswordReset(email: String, onComplete: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, "Email reset password telah dikirim.")
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun changePassword(newPassword: String, onComplete: (Boolean, String?) -> Unit) {
        val user: FirebaseUser? = auth.currentUser

        if (user != null) {
            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onComplete(true, "Password berhasil diubah.")
                    } else {
                        onComplete(false, task.exception?.message)
                    }
                }
        } else {
            onComplete(false, "Pengguna belum login.")
        }
    }
}

//    fun resetPassword(email: String, onComplete: (Boolean, String?) -> Unit) {
//        auth.sendPasswordResetEmail(email)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    onComplete(true, null)
//                } else {
//                    onComplete(false, task.exception?.message)
//                }
//            }
//    }
