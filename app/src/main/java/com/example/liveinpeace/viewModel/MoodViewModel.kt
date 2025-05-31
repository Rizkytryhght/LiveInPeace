package com.example.liveinpeace.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveinpeace.data.repository.MoodRepository
import com.example.liveinpeace.model.MoodEntry
import kotlinx.coroutines.launch

class MoodViewModel : ViewModel() {
    private val repository = MoodRepository()
    private val _moods = mutableStateOf<List<MoodEntry>>(emptyList())
    val moods: State<List<MoodEntry>> = _moods
    private val _hasSubmittedToday = mutableStateOf(false)
    val hasSubmittedToday: State<Boolean> = _hasSubmittedToday
    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading
    private var isDataLoaded = false
    private var lastSaveTime = 0L

    init {
        loadData()
    }

    private fun loadData() {
        if (isDataLoaded) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _hasSubmittedToday.value = repository.hasSubmittedMoodToday()
                _moods.value = repository.getMoodsLast7Days()
                isDataLoaded = true
                println("Loaded moods: ${_moods.value.size}, submitted today: ${_hasSubmittedToday.value}")
            } catch (e: Exception) {
                println("Error loading data: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveMood(mood: String) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastSaveTime < 1000 || _hasSubmittedToday.value) {
            println("Save mood skipped: Too soon or already submitted")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                println("Attempting to save mood: $mood")
                val success = repository.saveMood(mood)
                if (success) {
                    _hasSubmittedToday.value = true
                    _moods.value = repository.getMoodsLast7Days()
                    lastSaveTime = currentTime
                    println("Saved mood: $mood")
                } else {
                    println("Failed to save mood: $mood")
                }
            } catch (e: Exception) {
                println("Error saving mood: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}