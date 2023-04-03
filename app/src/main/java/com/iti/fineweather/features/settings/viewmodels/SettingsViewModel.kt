package com.iti.fineweather.features.settings.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.fineweather.core.helpers.Resource
import com.iti.fineweather.core.helpers.UiState
import com.iti.fineweather.features.common.repositories.UserPreferencesRepository
import com.iti.fineweather.features.settings.models.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<UiState<UserPreferences>> by lazy {
        userPreferencesRepository.userPreferencesFlow.map { result ->
            when (result) {
                is Resource.Success -> UiState.Loaded(result.data)
                is Resource.Error ->  UiState.Error(result.error)
            }
        }.stateIn(viewModelScope, started = SharingStarted.Lazily, initialValue = UiState.Loading())
    }

    fun updateLocationType(locationType: UserPreferences.LocationType) {
        viewModelScope.launch { 
            userPreferencesRepository.updateLocationType(locationType)
        }
    }

    fun updateLocation(location: UserPreferences.MapPlace) {
        viewModelScope.launch {
            userPreferencesRepository.updateLocation(location)
        }
    }

    fun updateTemperatureUnit(temperatureUnit: UserPreferences.TemperatureUnit) {
        viewModelScope.launch {
            userPreferencesRepository.updateTemperatureUnit(temperatureUnit)
        }
    }

    fun updateWindSpeedUnit(windSpeedUnit: UserPreferences.WindSpeedUnit) {
        viewModelScope.launch {
            userPreferencesRepository.updateWindSpeedUnit(windSpeedUnit)
        }
    }

    fun updateLanguage(language: UserPreferences.Language) {
        viewModelScope.launch {
            userPreferencesRepository.updateLanguage(language)
        }
    }
}
