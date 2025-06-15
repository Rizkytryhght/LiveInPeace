package com.example.liveinpeace.data.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
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

    // Google Sign-In client
    private var googleSignInClient: GoogleSignInClient? = null

    // Web Client ID dari Firebase Console
    private val webClientId = "1064631648244-942acv18q4mhav6dbjefj7u5qheo5949.apps.googleusercontent.com" // Ganti dengan Web Client ID yang sebenarnya

    // Initialize Google Sign-In
    fun initializeGoogleSignIn(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
        Log.d(tag, "Google Sign-In initialized")
    }

    // Get Google Sign-In client
    fun getGoogleSignInClient(): GoogleSignInClient? {
        return googleSignInClient
    }

    // Sign in with Google
    fun signInWithGoogle(idToken: String, onComplete: (Boolean, String?) -> Unit) {
        Log.d(tag, "Attempting Google Sign-In with ID token")
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid

                    if (uid != null) {
                        Log.d(tag, "Google Sign-In successful for UID: $uid")

                        // Check if user exists in Firestore
                        db.collection("users").document(uid).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    Log.d(tag, "Existing Google user found in Firestore")
                                    onComplete(true, null)
                                } else {
                                    Log.d(tag, "New Google user, creating Firestore document")
                                    // Create new user document for Google Sign-In user
                                    createGoogleUserDocument(user, onComplete)
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e(tag, "Failed to check Google user in Firestore: ${e.message}")
                                onComplete(false, "Gagal memeriksa akun: ${e.message}")
                            }
                    } else {
                        Log.e(tag, "Google Sign-In successful but user is null")
                        onComplete(false, "User tidak ditemukan setelah login Google")
                    }
                } else {
                    Log.e(tag, "Google Sign-In failed: ${task.exception?.message}")
                    onComplete(false, task.exception?.message)
                }
            }
    }

    // Create user document for Google Sign-In users
    private fun createGoogleUserDocument(user: FirebaseUser, onComplete: (Boolean, String?) -> Unit) {
        val uid = user.uid
        val email = user.email ?: ""
        val displayName = user.displayName ?: ""

        // Split display name into first and last name
        val nameParts = displayName.split(" ")
        val firstName = nameParts.firstOrNull() ?: ""
        val lastName = if (nameParts.size > 1) nameParts.drop(1).joinToString(" ") else ""

        val userMap = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "gender" to "", // Empty for Google users, can be filled later
            "phoneNumber" to "", // Empty for Google users, can be filled later
            "signInMethod" to "google"
        )

        Log.d(tag, "Creating Google user document: $userMap")
        db.collection("users").document(uid).set(userMap)
            .addOnSuccessListener {
                Log.d(tag, "Google user document created successfully")
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                Log.e(tag, "Failed to create Google user document: ${e.message}")
                onComplete(false, "Gagal membuat profil pengguna: ${e.message}")
            }
    }

    // Sign out (including Google Sign-In)
    fun signOut(context: Context, onComplete: () -> Unit) {
        // Sign out from Firebase
        auth.signOut()

        // Sign out from Google
        googleSignInClient?.signOut()?.addOnCompleteListener {
            Log.d(tag, "Signed out from Google and Firebase")
            onComplete()
        } ?: run {
            Log.d(tag, "Signed out from Firebase only")
            onComplete()
        }
    }

    // Check if user is logged in
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // Get current user
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        Log.d("AuthRepository", "Attempting login with email: $email")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AuthRepository", "Firebase Auth success for email: $email")
                    val user = auth.currentUser
                    val uid = user?.uid
                    if (uid != null) {
                        Log.d("AuthRepository", "Checking Firestore for UID: $uid")
                        db.collection("users").document(uid).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    Log.d("AuthRepository", "User document found for UID: $uid")
                                    onComplete(true, null)
                                } else {
                                    Log.e("AuthRepository", "User document not found for UID: $uid")
                                    auth.signOut()
                                    onComplete(false, "Akun tidak ditemukan. Silakan registrasi.")
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("AuthRepository", "Firestore check failed: ${e.message}")
                                auth.signOut()
                                onComplete(false, "Gagal memeriksa akun: ${e.message}")
                            }
                    } else {
                        Log.e("AuthRepository", "User not found after login")
                        onComplete(false, "User tidak ditemukan.")
                    }
                } else {
                    Log.e("AuthRepository", "Login failed: ${task.exception?.message}")
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
                            "phoneNumber" to phoneNumber,
                            "signInMethod" to "email"
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