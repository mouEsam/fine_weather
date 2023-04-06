package com.iti.fineweather.features.alerts.services.local

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.iti.fineweather.features.alerts.entities.RepetitionType
import com.iti.fineweather.features.alerts.repositories.WeatherAlertsRepository
import com.iti.fineweather.features.common.helpers.languageCode
import com.iti.fineweather.features.common.repositories.UserPreferencesRepository
import com.iti.fineweather.features.weather.services.remote.WeatherRemoteService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate

@HiltWorker
class WeatherAlertsWorker @AssistedInject constructor(
    @Assisted applicationContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val weatherAlertsNotifier: WeatherAlertsNotifier,
    private val weatherAlertsRepository: WeatherAlertsRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val weatherRemoteService: WeatherRemoteService,
) : CoroutineWorker(applicationContext, workerParameters) {
    override suspend fun doWork(): Result {
        val alertId = id
        setForeground(weatherAlertsNotifier.createNotificationForegroundInfo(alertId))
        val alertPreferences = weatherAlertsRepository.getAlert(alertId).firstOrNull()?.data ?: return Result.failure()
        val userPreferences =
            userPreferencesRepository.userPreferencesFlow.firstOrNull()?.data ?: return Result.failure()
        val weatherData = try {
            weatherRemoteService.getWeather(
                latitude = userPreferences.location.location.latitude,
                longitude = userPreferences.location.location.longitude,
                language = userPreferences.language.languageCode,
            )
        } catch (_: Exception) {
            return Result.retry()
        }
        weatherData.alerts?.forEach { alert ->
            weatherAlertsNotifier.notifyForAlert(alert, alertPreferences)
        } ?: weatherAlertsNotifier.notifyForNoAlerts(alertPreferences)
        if (
            alertPreferences.repetitionType == RepetitionType.SINGLE ||
            alertPreferences.endDate?.isAfter(LocalDate.now()) == false
        ) {
            weatherAlertsRepository.setExhausted(alertPreferences)
        }
        return Result.success()
    }
}
