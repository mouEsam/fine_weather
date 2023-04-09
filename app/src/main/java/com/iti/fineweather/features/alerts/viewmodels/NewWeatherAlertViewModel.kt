package com.iti.fineweather.features.alerts.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.util.VisibleForTesting
import com.iti.fineweather.R
import com.iti.fineweather.core.helpers.InvalidStateException
import com.iti.fineweather.core.helpers.MissingValueException
import com.iti.fineweather.core.helpers.UiState
import com.iti.fineweather.core.utils.wrap
import com.iti.fineweather.features.alerts.entities.RepetitionType
import com.iti.fineweather.features.alerts.entities.UserWeatherAlert
import com.iti.fineweather.features.alerts.models.WeatherAlertTemplate
import com.iti.fineweather.features.alerts.repositories.WeatherAlertsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class NewWeatherAlertViewModel @Inject constructor(private val alertsRepository: WeatherAlertsRepository) :
    ViewModel() {

    private val _alert: MutableStateFlow<WeatherAlertTemplate?> = MutableStateFlow(null)
    val alert: StateFlow<WeatherAlertTemplate?> = _alert.asStateFlow()

    private val _operationState = MutableSharedFlow<UiState<Unit>>()
    val operationState =
        _operationState.stateIn(viewModelScope, SharingStarted.Lazily, UiState.Initial())

    fun newAlert() {
        viewModelScope.launch {
            _alert.emit(WeatherAlertTemplate())
        }
    }

    fun submit() {
        viewModelScope.launch {
            _operationState.wrap {
                val newAlert = getAlert()
                alertsRepository.addAlert(newAlert)
                _alert.emit(null)
            }
        }
    }

    fun validate(): Boolean {
        return try {
            getAlert()
            true
        } catch (e: Exception) {
            viewModelScope.launch {
                _operationState.emit(UiState.Error(e))
            }
            false
        }
    }

    fun resetError() {
        viewModelScope.launch {
            _operationState.emit(UiState.Initial())
        }
    }

    private fun getAlert(): UserWeatherAlert {
        val template = _alert.value ?: throw InvalidStateException(R.string.error_submit_before_add)
        var alert = UserWeatherAlert(
            alarmEnabled = template.alarmEnabled
                ?: throw MissingValueException(R.string.alerts_alert_type),
            repetitionType = template.repetitionType
                ?: throw MissingValueException(R.string.alerts_repetition_type),
            time = template.time ?: throw MissingValueException(R.string.alerts_time),
            startDate = template.startDate
                ?: throw MissingValueException(R.string.alerts_start_date),
            endDate = template.endDate
        )
        if (alert.endDate?.isBefore(alert.startDate) == true) {
            throw InvalidStateException(R.string.error_date_end_before_start)
        }
        if (alert.repetitionType == RepetitionType.SINGLE) {
            alert = alert.copy(endDate = null)
        }
        return alert
    }


    fun updateRepetitionType(repetitionType: RepetitionType) {
        update { template ->
            template.copy(repetitionType = repetitionType)
        }
    }

    fun updateAlarmEnabled(alarmEnabled: Boolean) {
        update { template -> template.copy(alarmEnabled = alarmEnabled) }
    }

    fun updateTime(time: LocalTime) {
        update { template -> template.copy(time = time) }
    }

    fun updateStartDate(startDate: LocalDate) {
        update { template -> template.copy(startDate = startDate) }
    }

    fun updateEndDate(endDate: LocalDate?) {
        update { template -> template.copy(endDate = endDate) }
    }

    @VisibleForTesting
    fun update(update: suspend (WeatherAlertTemplate) -> WeatherAlertTemplate?) {
        viewModelScope.launch {
            _operationState.wrap {
                val template =
                    _alert.value ?: throw InvalidStateException(R.string.error_submit_before_modify)
                _alert.emit(update(template))
            }
        }
    }
}
