package com.example.liveinpeace.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveinpeace.api.RetrofitInstance
import com.example.liveinpeace.model.SalatTimesResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ReminderViewModel : ViewModel() {

    // Prayer time StateFlows
    private val _fajrTime = MutableStateFlow("...")
    val fajrTime: StateFlow<String> = _fajrTime

    private val _dhuhrTime = MutableStateFlow("...")
    val dhuhrTime: StateFlow<String> = _dhuhrTime

    private val _asrTime = MutableStateFlow("...")
    val asrTime: StateFlow<String> = _asrTime

    private val _maghribTime = MutableStateFlow("...")
    val maghribTime: StateFlow<String> = _maghribTime

    private val _ishaTime = MutableStateFlow("...")
    val ishaTime: StateFlow<String> = _ishaTime

    // Keep dzikir times for backward compatibility
    private val _dzikirPagiTime = MutableStateFlow("...")
    val dzikirPagiTime: StateFlow<String> = _dzikirPagiTime

    private val _dzikirPetangTime = MutableStateFlow("...")
    val dzikirPetangTime: StateFlow<String> = _dzikirPetangTime

    // Track if reminders are enabled
    private val _isFajrEnabled = MutableStateFlow(true)
    val isFajrEnabled: StateFlow<Boolean> = _isFajrEnabled

    private val _isDhuhrEnabled = MutableStateFlow(true)
    val isDhuhrEnabled: StateFlow<Boolean> = _isDhuhrEnabled

    private val _isAsrEnabled = MutableStateFlow(true)
    val isAsrEnabled: StateFlow<Boolean> = _isAsrEnabled

    private val _isMaghribEnabled = MutableStateFlow(true)
    val isMaghribEnabled: StateFlow<Boolean> = _isMaghribEnabled

    private val _isIshaEnabled = MutableStateFlow(true)
    val isIshaEnabled: StateFlow<Boolean> = _isIshaEnabled

    private val _isPagiEnabled = MutableStateFlow(true)
    val isPagiEnabled: StateFlow<Boolean> = _isPagiEnabled

    private val _isPetangEnabled = MutableStateFlow(true)
    val isPetangEnabled: StateFlow<Boolean> = _isPetangEnabled

    init {
        // Initialize with default values
        fetchTimings()
    }

    fun fetchTimings(city: String = "Bandung", country: String = "Indonesia") {
        // Reset all time values to loading state
        _fajrTime.value = "..."
        _dhuhrTime.value = "..."
        _asrTime.value = "..."
        _maghribTime.value = "..."
        _ishaTime.value = "..."
        _dzikirPagiTime.value = "..."
        _dzikirPetangTime.value = "..."

        viewModelScope.launch {
            RetrofitInstance.api.getTimingsByCity(city, country)
                .enqueue(object : Callback<SalatTimesResponse> {
                    override fun onResponse(
                        call: Call<SalatTimesResponse>,
                        response: Response<SalatTimesResponse>
                    ) {
                        if (response.isSuccessful) {
                            val timings = response.body()?.data?.timings
                            if (timings != null) {
                                // Set prayer times
                                _fajrTime.value = timings.Fajr
                                _dhuhrTime.value = timings.Dhuhr
                                _asrTime.value = timings.Asr
                                _maghribTime.value = timings.Maghrib
                                _ishaTime.value = timings.Isha

                                // For backward compatibility
                                _dzikirPagiTime.value = timings.Fajr
                                _dzikirPetangTime.value = timings.Maghrib
                            } else {
                                setErrorValues()
                            }
                        } else {
                            setErrorValues()
                        }
                    }

                    override fun onFailure(call: Call<SalatTimesResponse>, t: Throwable) {
                        setErrorValues("Failed")
                    }
                })
        }
    }

    private fun setErrorValues(errorMsg: String = "Error") {
        _fajrTime.value = errorMsg
        _dhuhrTime.value = errorMsg
        _asrTime.value = errorMsg
        _maghribTime.value = errorMsg
        _ishaTime.value = errorMsg
        _dzikirPagiTime.value = errorMsg
        _dzikirPetangTime.value = errorMsg
    }

    // Set reminder enabled/disabled
    fun setFajrEnabled(enabled: Boolean) {
        _isFajrEnabled.value = enabled
    }

    fun setDhuhrEnabled(enabled: Boolean) {
        _isDhuhrEnabled.value = enabled
    }

    fun setAsrEnabled(enabled: Boolean) {
        _isAsrEnabled.value = enabled
    }

    fun setMaghribEnabled(enabled: Boolean) {
        _isMaghribEnabled.value = enabled
    }

    fun setIshaEnabled(enabled: Boolean) {
        _isIshaEnabled.value = enabled
    }

    fun setDzikirPagiEnabled(enabled: Boolean) {
        _isPagiEnabled.value = enabled
    }

    fun setDzikirPetangEnabled(enabled: Boolean) {
        _isPetangEnabled.value = enabled
    }

    // Helper function to get formatted time for UI display
    fun getFormattedTime(calendar: Calendar): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(calendar.time)
    }
}