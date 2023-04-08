package com.iti.fineweather.features.alerts.services.local

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.iti.fineweather.features.alerts.entities.RepetitionType
import com.iti.fineweather.features.alerts.repositories.WeatherAlertsRepository
import com.iti.fineweather.features.common.repositories.UserPreferencesRepository
import com.iti.fineweather.features.settings.utils.toLocale
import com.iti.fineweather.features.weather.repositories.WeatherRepository
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
    private val weatherRepository: WeatherRepository,
) : CoroutineWorker(applicationContext, workerParameters) {
    override suspend fun doWork(): Result {
        val alertId = id
        setForeground(weatherAlertsNotifier.createNotificationForegroundInfo(alertId))
        val alertPreferences = weatherAlertsRepository.getAlert(alertId).firstOrNull()?.data ?: return Result.failure()
        val userPreferences =
            userPreferencesRepository.userPreferencesFlow.firstOrNull()?.data ?: return Result.failure()

        val weatherData = weatherRepository.getWeatherData(
            latitude = userPreferences.location.location.latitude,
            longitude = userPreferences.location.location.longitude,
            locale = userPreferences.language.toLocale(),
        ).data ?: return Result.retry()

        weatherAlertsNotifier.notifyForAlert(weatherData.alerts ?: listOf(), alertPreferences)

        if (
            alertPreferences.repetitionType == RepetitionType.SINGLE ||
            alertPreferences.endDate?.isAfter(LocalDate.now()) == false
        ) {
            weatherAlertsRepository.setExhausted(alertPreferences)
        }
        return Result.success()
    }
}
