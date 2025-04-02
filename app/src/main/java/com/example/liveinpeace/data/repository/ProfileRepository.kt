package com.example.liveinpeace.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.liveinpeace.data.ProfileModel

class ProfileRepository(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserProfile", Context.MODE_PRIVATE)

    fun getProfile(): ProfileModel {
        val firstName = sharedPreferences.getString("first_name", "") ?: ""
        val lastName = sharedPreferences.getString("last_name", "") ?: ""
        val profileImageUri = sharedPreferences.getString("profile_image", "") ?: ""

        return ProfileModel(firstName, lastName, "", "", "", profileImageUri)
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

    fun loadProfile(): ProfileModel {
        val firstName = sharedPreferences.getString("firstName", "") ?: ""
        val lastName = sharedPreferences.getString("lastName", "") ?: ""
        val profileImageUri = sharedPreferences.getString("profileImageUri", "") ?: ""

        return ProfileModel(firstName, lastName, "", "", "", profileImageUri)
    }

    fun clearProfileData() {
        sharedPreferences.edit().clear().apply()
    }
}