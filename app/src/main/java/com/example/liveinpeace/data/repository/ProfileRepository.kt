package com.example.liveinpeace.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.liveinpeace.data.ProfileModel
import com.example.liveinpeace.data.local.room.AppDatabase
import java.io.File
import java.io.FileOutputStream

class ProfileRepository(private val context: Context) {
    private val profileDao = AppDatabase.getDatabase(context).profileDao()

    suspend fun getProfile(): ProfileModel {
        val profileEntity = profileDao.getProfile()
        Log.d("ProfileRepository", "Mengambil profil: $profileEntity")
        return ProfileModel.fromEntity(profileEntity)
    }

    suspend fun saveProfile(profile: ProfileModel) {
        val existingProfile = profileDao.getProfile()
        val profileEntity = profile.toEntity()
        if (existingProfile == null) {
            Log.d("ProfileRepository", "Menyimpan profil baru: $profileEntity")
            profileDao.insertProfile(profileEntity)
        } else {
            Log.d("ProfileRepository", "Memperbarui profil: $profileEntity")
            profileDao.updateProfile(profileEntity)
        }
    }

    suspend fun saveProfileImage(uri: Uri): String {
        try {
            // Salin file ke penyimpanan internal
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Log.e("ProfileRepository", "Gagal membuka input stream untuk URI: $uri")
                return ""
            }
            val file = File(context.filesDir, "profile_image.jpg")
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
            inputStream.close()

            // Perbarui profil dengan path baru
            val currentProfile = getProfile()
            val updatedProfile = currentProfile.copy(profileImagePath = file.absolutePath)
            saveProfile(updatedProfile)
            Log.d("ProfileRepository", "Foto disimpan di: ${file.absolutePath}")
            return file.absolutePath
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Gagal menyimpan foto: ${e.message}", e)
            return ""
        }
    }

    suspend fun clearProfileData() {
        profileDao.clearProfile()
        val file = File(context.filesDir, "profile_image.jpg")
        if (file.exists()) {
            file.delete()
            Log.d("ProfileRepository", "File foto dihapus: ${file.absolutePath}")
        }
    }
}