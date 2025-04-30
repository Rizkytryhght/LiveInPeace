package com.example.liveinpeace.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveinpeace.data.repository.MoodRepository
import com.example.liveinpeace.model.MoodEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MoodViewModel : ViewModel() {
    private val repository = MoodRepository()

    private val _moods = MutableStateFlow<List<MoodEntry>>(emptyList())
    val moods: StateFlow<List<MoodEntry>> = _moods

    fun saveMood(mood: String) {
        viewModelScope.launch {
            repository.saveMood(mood)
            loadMoods()
        }
    }

    fun loadMoods() {
        viewModelScope.launch {
            _moods.value = repository.getAllMoods()
        }
    }
}