package com.iti.fineweather.features.alerts.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.fineweather.core.helpers.Resource
import com.iti.fineweather.core.helpers.UiState
import com.iti.fineweather.core.utils.wrap
import com.iti.fineweather.features.alerts.entities.UserWeatherAlert
import com.iti.fineweather.features.alerts.repositories.WeatherAlertsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherAlertsViewModel @Inject constructor(private val alertsRepository: WeatherAlertsRepository): ViewModel() {

    val uiState: StateFlow<UiState<List<UserWeatherAlert>>> by lazy {
        alertsRepository.weatherAlertsFlow.map { result ->
            when (result) {
                is Resource.Success -> UiState.Loaded(result.data)
                is Resource.Error ->  UiState.Error(result.error)
            }
        }.stateIn(viewModelScope, started = SharingStarted.Lazily, initialValue = UiState.Loading())
    }

    private val _operationState = MutableSharedFlow<UiState<Unit>>()
    val operationState = _operationState.stateIn(viewModelScope, SharingStarted.Lazily, UiState.Initial())

    fun addAlert(alert: UserWeatherAlert) {
        viewModelScope.launch {
            _operationState.wrap {
                alertsRepository.addAlert(alert)
            }
        }
    }

    fun deleteAlert(alert: UserWeatherAlert) {
        viewModelScope.launch {
            _operationState.wrap {
                alertsRepository.removeAlert(alert)
            }
        }
    }

    fun enableAlarm(alert: UserWeatherAlert, enabled: Boolean) {
        viewModelScope.launch {
            _operationState.wrap {
                alertsRepository.updateAlertAlarmEnabled(alert, enabled)
            }
        }
    }

}
