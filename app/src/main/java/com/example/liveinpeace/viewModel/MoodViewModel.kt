package com.example.liveinpeace.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveinpeace.data.repository.MoodRepository
import com.example.liveinpeace.model.MoodEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MoodViewModel : ViewModel() {
    private val repository = MoodRepository()

    val moods: Flow<List<MoodEntry>> = repository.getAllMoods()

    fun saveMood(mood: String) {
        viewModelScope.launch {
            try {
                repository.saveMood(mood)
            } catch (e: Exception) {
                println("Error saving mood in ViewModel: ${e.message}")
            }
        }
    }
}
