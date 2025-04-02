package com.example.liveinpeace

import android.app.Application

class LiveInPeaceApp : Application() {
    companion object {
        lateinit var instance: LiveInPeaceApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
