package com.example.liveinpeace.utils

import android.content.Context

object OnboardingPreferences {
    private const val PREF_NAME = "onboarding_pref"
    private const val KEY_COMPLETED = "is_completed"

    fun isCompleted(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_COMPLETED, false)
    }

    fun setCompleted(context: Context, completed: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_COMPLETED, completed).apply()
    }
}

