package com.iti.fineweather.features.alerts.repositories

import com.iti.fineweather.R
import com.iti.fineweather.core.di.IODispatcher
import com.iti.fineweather.core.helpers.InvalidStateException
import com.iti.fineweather.core.helpers.Resource
import com.iti.fineweather.features.alerts.entities.UserWeatherAlert
import com.iti.fineweather.features.alerts.services.local.WeatherAlertScheduler
import com.iti.fineweather.features.alerts.services.local.WeatherAlertsDAO
import com.iti.fineweather.features.settings.models.UserPreferences.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherAlertsRepository @Inject constructor(
    private val weatherAlertsDAO: WeatherAlertsDAO,
    private val weatherAlertScheduler: WeatherAlertScheduler,
    @IODispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    val weatherAlertsFlow: Flow<Resource<List<UserWeatherAlert>>> by lazy {
        weatherAlertsDAO.getAllActive()
            .map<_, Resource<List<UserWeatherAlert>>> { alerts ->
                Resource.Success.Local(alerts)
            }.catch { exception ->
                when (exception) {
                    is Exception -> emit(Resource.Error(exception))
                    else -> throw exception
                }
            }.flowOn(dispatcher)
    }

    suspend fun getAlert(id: UUID): Flow<Resource<UserWeatherAlert>> {
        return weatherAlertsDAO.getById(id).map<_, Resource<UserWeatherAlert>> { alerts ->
            Resource.Success.Local(alerts)
        }.catch { exception ->
            when (exception) {
                is Exception -> emit(Resource.Error(exception))
                else -> throw exception
            }
        }.flowOn(dispatcher)
    }

    suspend fun addAlert(alert: UserWeatherAlert) {
        withContext(dispatcher) {
            weatherAlertsDAO.insertAll(alert)
            weatherAlertScheduler.scheduleAlert(alert)
        }
    }

    suspend fun removeAlert(alert: UserWeatherAlert) {
        withContext(dispatcher) {
            if (alert.deletedAt != null) {
                throw InvalidStateException(R.string.error_delete_deleted_alarm)
            }
            weatherAlertsDAO.updateAll(alert.copy(deletedAt = LocalDateTime.now()))
            weatherAlertScheduler.cancelAlert(alert)
        }
    }

    suspend fun setExhausted(alert: UserWeatherAlert) {
        withContext(dispatcher) {
            if (alert.exhausted) {
                throw InvalidStateException(R.string.error_exhaust_exhausted_alarm)
            }
            weatherAlertsDAO.updateAll(alert.copy(exhausted = true))
            weatherAlertScheduler.cancelAlert(alert)
        }
    }

    suspend fun updateAlertAlarmEnabled(alert: UserWeatherAlert, alarmEnabled: Boolean) {
        withContext(dispatcher) {
            weatherAlertsDAO.updateAll(alert.copy(alarmEnabled = alarmEnabled))
        }
    }

}
