package com.example.liveinpeace.data.repository

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var verificationId: String? = null

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

    fun register(email: String, password: String, firstName: String, lastName: String, onComplete: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid
//                    onComplete(true, null)

                    if (uid != null) {
                        val db = FirebaseFirestore.getInstance()
                        val userMap = hashMapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "email" to email
                        )

                        db.collection("users").document(uid).set(userMap)
                            .addOnSuccessListener {
                                onComplete(true, null)
                            }
                            .addOnFailureListener { e ->
                                onComplete(
                                    false,
                                    "Registrasi berhasil, tapi gagal simpan data: ${e.message}"
                                )
                            }
                    } else {
                        onComplete(false, "Registrasi berhasil, tapi user tidak ditemukan.")
                    }
                }else{
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

    fun resetPassword(email: String, onComplete: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login berhasil
                    println("Login berhasil dengan OTP")
                } else {
                    // Login gagal
                    println("Login gagal dengan OTP: ${task.exception?.message}")
                }
            }
    }

    fun sendOtp(phoneNumber: String, activity: Activity, onComplete: (Boolean, String?) -> Unit) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)        // Nomor telepon pengguna
            .setTimeout(60L, TimeUnit.SECONDS)   // Waktu kadaluarsa OTP
            .setActivity(activity)               // Activity yang memulai verifikasi
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // OTP otomatis diterima, langsung login
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    onComplete(false, e.message)
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    // Simpan verificationId untuk digunakan saat verifikasi OTP
                    this@AuthRepository.verificationId = verificationId
                    onComplete(true, "OTP telah dikirim.")
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(otp: String, onComplete: (Boolean) -> Unit) {
        val verificationId = this.verificationId // Ambil verificationId yang telah disimpan
        if (verificationId == null) {
            onComplete(false)  // Tidak ada verificationId yang tersimpan
            return
        }

        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
    }
}


