package com.example.liveinpeace.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.liveinpeace.data.ProfileModel

class ProfileRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserProfile", Context.MODE_PRIVATE)

    fun getProfile(): ProfileModel {
        return ProfileModel(
            firstName = sharedPreferences.getString("first_name", "") ?: "",
            lastName = sharedPreferences.getString("last_name", "") ?: "",
            profileImageUri = sharedPreferences.getString("profile_image", "") ?: ""
        )
    }

    fun saveProfile(profile: ProfileModel) {
        sharedPreferences.edit().apply {
            putString("first_name", profile.firstName)
            putString("last_name", profile.lastName)
            putString("profile_image", profile.profileImageUri)
            apply()
        }
    }

    fun saveProfileImage(uri: String) {
        sharedPreferences.edit().putString("profile_image", uri).apply()
    }

    fun clearProfileData() {
        sharedPreferences.edit().clear().apply()
    }
}
