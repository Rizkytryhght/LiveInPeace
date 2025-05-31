package com.example.liveinpeace.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.liveinpeace.data.ProfileModel
import com.example.liveinpeace.data.local.room.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class ProfileRepository(private val context: Context) {
    private val profileDao = AppDatabase.getDatabase(context).profileDao()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val tag = "ProfileRepository"

    suspend fun getProfile(): ProfileModel {
        val profileEntity = profileDao.getProfile()
        Log.d(tag, "Mengambil profil dari Room DB: $profileEntity")
        if (profileEntity != null && profileEntity.email.isNotEmpty()) {
            if (profileEntity.profileImagePath.isNotEmpty() && !File(profileEntity.profileImagePath).exists()) {
                Log.w(tag, "File foto lokal tidak ada: ${profileEntity.profileImagePath}, sinkron ulang")
                return syncProfileFromFirestore()
            }
            return ProfileModel.fromEntity(profileEntity)
        }
        Log.d(tag, "Room DB kosong, mencoba sinkron dari Firestore")
        return syncProfileFromFirestore()
    }

    suspend fun saveProfile(profile: ProfileModel) {
        val existingProfile = profileDao.getProfile()
        val profileEntity = profile.toEntity()
        if (existingProfile == null) {
            Log.d(tag, "Menyimpan profil baru: $profileEntity")
            profileDao.insertProfile(profileEntity)
        } else {
            Log.d(tag, "Memperbarui profil: $profileEntity")
            profileDao.updateProfile(profileEntity)
        }
        updateProfileToFirestore(profile)
    }

    suspend fun syncProfileFromFirestore(): ProfileModel {
        val user = auth.currentUser
        if (user == null) {
            Log.e(tag, "Tidak ada pengguna terautentikasi")
            return ProfileModel()
        }

        val uid = user.uid
        Log.d(tag, "Sinkronisasi profil dari Firestore untuk UID: $uid")
        return try {
            val document = db.collection("users").document(uid).get().await()
            if (document.exists()) {
                val profileImageUrl = document.getString("profileImageUrl") ?: ""
                var localImagePath = ""
                if (profileImageUrl.isNotEmpty()) {
                    try {
                        val file = File(context.filesDir, "profile_image_${uid}.jpg")
                        withContext(Dispatchers.IO) {
                            URL(profileImageUrl).openStream()
                        }.use { input ->
                            FileOutputStream(file).use { output ->
                                input.copyTo(output)
                            }
                        }
                        localImagePath = file.absolutePath
                        Log.d(tag, "Foto diunduh ke: $localImagePath")
                    } catch (e: Exception) {
                        Log.e(tag, "Gagal mengunduh foto dari URL: ${e.message}", e)
                    }
                }

                val profile = ProfileModel(
                    firstName = document.getString("firstName") ?: "",
                    lastName = document.getString("lastName") ?: "",
                    email = document.getString("email") ?: "",
                    gender = document.getString("gender") ?: "",
                    phoneNumber = document.getString("phoneNumber") ?: "",
                    profileImagePath = localImagePath
                )
                Log.d(tag, "Data dari Firestore: $profile")
                if (profile.email.isNotEmpty()) {
                    saveProfile(profile)
                }
                profile
            } else {
                Log.w(tag, "Dokumen Firestore tidak ditemukan untuk UID: $uid")
                ProfileModel()
            }
        } catch (e: Exception) {
            Log.e(tag, "Gagal sinkronisasi dari Firestore: ${e.message}", e)
            ProfileModel()
        }
    }

    suspend fun saveProfileImage(uri: Uri): String {
        try {
            val user = auth.currentUser
            if (user == null) {
                Log.e(tag, "Tidak ada pengguna terautentikasi")
                return ""
            }
            val uid = user.uid

            // Simpan lokal
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Log.e(tag, "Gagal membuka input stream untuk URI: $uri")
                return ""
            }
            val file = File(context.filesDir, "profile_image_${uid}.jpg")
            withContext(Dispatchers.IO) {
                FileOutputStream(file).use { output ->
                    inputStream.copyTo(output)
                }
            }
            withContext(Dispatchers.IO) {
                inputStream.close()
            }
            val localPath = file.absolutePath
            Log.d(tag, "Foto disimpan lokal di: $localPath")

            // Upload ke Firebase Storage
            val storageRef = storage.reference.child("users/$uid/profile_image.jpg")
            try {
                storageRef.putFile(uri).await()
                val downloadUrl = storageRef.downloadUrl.await().toString()
                Log.d(tag, "Foto diupload ke Storage, URL: $downloadUrl")

                // Update Firestore dengan URL
                db.collection("users").document(uid)
                    .update("profileImageUrl", downloadUrl)
                    .await()
                Log.d(tag, "URL foto disimpan di Firestore")

                return localPath
            } catch (e: Exception) {
                Log.e(tag, "Gagal upload foto ke Storage: ${e.message}", e)
                return localPath
            }
        } catch (e: Exception) {
            Log.e(tag, "Gagal menyimpan foto: ${e.message}", e)
            return ""
        }
    }

    private suspend fun updateProfileToFirestore(profile: ProfileModel) {
        val user = auth.currentUser
        if (user == null) {
            Log.e(tag, "Tidak ada pengguna terautentikasi")
            return
        }
        val uid = user.uid
        try {
            val userMap = hashMapOf(
                "firstName" to profile.firstName,
                "lastName" to profile.lastName,
                "email" to profile.email,
                "gender" to profile.gender,
                "phoneNumber" to profile.phoneNumber
            )
            db.collection("users").document(uid).update(userMap as Map<String, Any>).await()
            Log.d(tag, "Profil disinkron ke Firestore: $userMap")
        } catch (e: Exception) {
            Log.e(tag, "Gagal sinkron profil ke Firestore: ${e.message}", e)
        }
    }

    suspend fun clearProfileData() {
        profileDao.clearProfile()
        val user = auth.currentUser
        val file = File(context.filesDir, "profile_image_${user?.uid}.jpg")
        if (file.exists()) {
            file.delete()
            Log.d(tag, "File foto dihapus: ${file.absolutePath}")
        }
    }
}