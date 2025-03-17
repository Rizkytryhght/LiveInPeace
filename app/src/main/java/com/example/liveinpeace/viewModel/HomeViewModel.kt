package com.example.liveinpeace.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val _welcomeMessage = MutableStateFlow("Welcome to LiveInPeace")
    val welcomeMessage: StateFlow<String> = _welcomeMessage
}
