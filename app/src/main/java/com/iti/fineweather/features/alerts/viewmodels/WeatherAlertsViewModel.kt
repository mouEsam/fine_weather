package com.iti.fineweather.features.alerts.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.fineweather.core.helpers.Resource
import com.iti.fineweather.core.helpers.UiState
import com.iti.fineweather.features.alerts.entities.UserWeatherAlert
import com.iti.fineweather.features.alerts.repositories.WeatherAlertsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherAlertsViewModel @Inject constructor(private val alertsRepository: WeatherAlertsRepository): ViewModel() {

    private val uiState: SharedFlow<UiState<List<UserWeatherAlert>>> by lazy {
        alertsRepository.weatherAlertsFlow.map { result ->
            when (result) {
                is Resource.Success -> UiState.Loaded(result.data)
                is Resource.Error ->  UiState.Error(result.error)
            }
        }.shareIn(viewModelScope, started = SharingStarted.Lazily, replay = 1)
    }

    private val _operationState = MutableSharedFlow<UiState<Unit>>()
    val operationState = _operationState.asSharedFlow()

    fun addAlert(alert: UserWeatherAlert) {
        viewModelScope.launch {
            wrapOperation {
                alertsRepository.addAlert(alert)
            }
        }
    }

    fun deleteAlert(alert: UserWeatherAlert) {
        viewModelScope.launch {
            wrapOperation {
                alertsRepository.removeAlert(alert)
            }
        }
    }

    fun enableAlarm(alert: UserWeatherAlert, enabled: Boolean) {
        viewModelScope.launch {
            wrapOperation {
                alertsRepository.updateAlertAlarmEnabled(alert, enabled)
            }
        }
    }

    private suspend fun <T> wrapOperation(operation: suspend () -> T): UiState<T> {
        _operationState.tryEmit(UiState.Loading())
        return try {
            coroutineScope {
                UiState.Loaded(operation())
           }
        } catch (e: Exception) {
            UiState.Error(e)
        }
    }
}
