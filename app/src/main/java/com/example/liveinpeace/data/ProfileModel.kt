package com.example.liveinpeace.data

import com.example.liveinpeace.data.local.room.ProfileEntity

data class ProfileModel(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val gender: String = "",
    val profileImagePath: String = "" // Ubah dari profileImageUri ke profileImagePath
) {
    fun toEntity(): ProfileEntity {
        return ProfileEntity(
            id = 1,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phoneNumber = phoneNumber,
            gender = gender,
            profileImagePath = profileImagePath
        )
    }

    companion object {
        fun fromEntity(entity: ProfileEntity?): ProfileModel {
            return if (entity != null) {
                ProfileModel(
                    firstName = entity.firstName,
                    lastName = entity.lastName,
                    email = entity.email,
                    phoneNumber = entity.phoneNumber,
                    gender = entity.gender,
                    profileImagePath = entity.profileImagePath
                )
            } else {
                ProfileModel()
            }
        }
    }
}