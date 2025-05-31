package com.example.liveinpeace.data.repository

import android.app.Activity
import android.util.Log
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
    private val tag = "AuthRepository"
    private val db = FirebaseFirestore.getInstance()

    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid
                    if (uid != null) {
                        Log.d(tag, "Firebase Auth berhasil untuk email: $email, UID: $uid")
                        // Cek apakah dokumen user ada di Firestore
                        db.collection("users").document(uid).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    Log.d(tag, "Dokumen user ditemukan di Firestore untuk UID: $uid")
                                    onComplete(true, null)
                                } else {
                                    Log.e(tag, "Dokumen user tidak ditemukan di Firestore untuk UID: $uid")
                                    auth.signOut() // Logout kalau nggak ada di Firestore
                                    onComplete(false, "Akun tidak ditemukan. Silakan registrasi.")
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e(tag, "Gagal memeriksa Firestore: ${e.message}")
                                auth.signOut()
                                onComplete(false, "Gagal memeriksa akun: ${e.message}")
                            }
                    } else {
                        Log.e(tag, "User tidak ditemukan setelah login")
                        onComplete(false, "User tidak ditemukan.")
                    }
                } else {
                    Log.e(tag, "Login gagal: ${task.exception?.message}")
                    onComplete(false, task.exception?.message)
                }
            }
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
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid

                    if (uid != null) {
                        Log.d(tag, "Registrasi berhasil, UID: $uid")
                        val userMap = hashMapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "email" to email,
                            "gender" to gender,
                            "phoneNumber" to phoneNumber
                        )

                        Log.d(tag, "Menyimpan data ke Firestore: $userMap")
                        db.collection("users").document(uid).set(userMap)
                            .addOnSuccessListener {
                                Log.d(tag, "Data berhasil disimpan ke Firestore untuk UID: $uid")
                                onComplete(true, null)
                            }
                            .addOnFailureListener { e ->
                                Log.e(tag, "Gagal simpan data ke Firestore: ${e.message}")
                                onComplete(
                                    false,
                                    "Registrasi berhasil, tapi gagal simpan data: ${e.message}"
                                )
                            }
                    } else {
                        Log.e(tag, "Registrasi berhasil, tapi user tidak ditemukan")
                        onComplete(false, "Registrasi berhasil, tapi user tidak ditemukan.")
                    }
                } else {
                    Log.e(tag, "Registrasi gagal: ${task.exception?.message}")
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun sendPasswordReset(email: String, onComplete: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(tag, "Email reset password dikirim ke: $email")
                    onComplete(true, "Email reset password telah dikirim.")
                } else {
                    Log.e(tag, "Gagal kirim email reset: ${task.exception?.message}")
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
                        Log.d(tag, "Password berhasil diubah untuk UID: ${user.uid}")
                        onComplete(true, "Password berhasil diubah.")
                    } else {
                        Log.e(tag, "Gagal ubah password: ${task.exception?.message}")
                        onComplete(false, task.exception?.message)
                    }
                }
        } else {
            Log.e(tag, "Gagal ubah password: Pengguna belum login")
            onComplete(false, "Pengguna belum login.")
        }
    }

    fun resetPassword(email: String, onComplete: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(tag, "Email reset password dikirim ke: $email")
                    onComplete(true, null)
                } else {
                    Log.e(tag, "Gagal kirim email reset: ${task.exception?.message}")
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(tag, "Login berhasil dengan OTP")
                } else {
                    Log.e(tag, "Login gagal dengan OTP: ${task.exception?.message}")
                }
            }
    }

    fun sendOtp(phoneNumber: String, activity: Activity, onComplete: (Boolean, String?) -> Unit) {
        Log.d(tag, "Mengirim OTP ke: $phoneNumber")
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d(tag, "Verifikasi OTP otomatis selesai")
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e(tag, "Verifikasi OTP gagal: ${e.message}")
                    onComplete(false, e.message)
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    Log.d(tag, "OTP dikirim, verificationId: $verificationId")
                    this@AuthRepository.verificationId = verificationId
                    onComplete(true, "OTP telah dikirim.")
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(otp: String, onComplete: (Boolean) -> Unit) {
        val verificationId = this.verificationId
        if (verificationId == null) {
            Log.e(tag, "Verifikasi OTP gagal: verificationId null")
            onComplete(false)
            return
        }

        Log.d(tag, "Memverifikasi OTP: $otp")
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(tag, "Verifikasi OTP berhasil")
                    onComplete(true)
                } else {
                    Log.e(tag, "Verifikasi OTP gagal: ${task.exception?.message}")
                    onComplete(false)
                }
            }
    }
}