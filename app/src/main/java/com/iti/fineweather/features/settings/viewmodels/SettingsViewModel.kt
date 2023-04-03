package com.iti.fineweather.features.settings.viewmodels

import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.fineweather.core.helpers.Resource
import com.iti.fineweather.core.helpers.UiState
import com.iti.fineweather.core.utils.wrapResource
import com.iti.fineweather.features.common.repositories.UserPreferencesRepository
import com.iti.fineweather.features.map.models.MapPlaceResult
import com.iti.fineweather.features.settings.models.UserPreferences
import com.iti.fineweather.features.settings.repositories.GpsPlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val gpsPlaceRepository: GpsPlaceRepository
) : ViewModel() {

    val uiState: StateFlow<UiState<UserPreferences>> by lazy {
        userPreferencesRepository.userPreferencesFlow.map { result ->
            when (result) {
                is Resource.Success -> UiState.Loaded(result.data)
                is Resource.Error ->  UiState.Error(result.error)
            }
        }.stateIn(viewModelScope, started = SharingStarted.Lazily, initialValue = UiState.Loading())
    }

    private var job: Job? = null
    private val _operationState = MutableSharedFlow<UiState<Any>>()
    val operationState = _operationState.stateIn(viewModelScope, SharingStarted.Lazily, UiState.Initial())

    @RequiresPermission(
        anyOf = [
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
        ]
    )
    fun updateGpsLocation() {
        viewModelScope.launch {
            _operationState.wrapResource {
                val locationResult = gpsPlaceRepository.getLocation()
                if (locationResult is Resource.Success) {
                    userPreferencesRepository.updateGpsLocation(locationResult.data.toMapPlace())
                }
                Timber.d("UPDATED $locationResult")
                locationResult
            }
        }
    }

    fun updateMapLocation(location: MapPlaceResult) {
        viewModelScope.launch {
            userPreferencesRepository.updateMapLocation(location.toMapPlace())
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
