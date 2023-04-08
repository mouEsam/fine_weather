package com.iti.fineweather.features.alerts.services.local

import android.content.Context
import androidx.work.*
import com.iti.fineweather.features.alerts.entities.RepetitionType
import com.iti.fineweather.features.alerts.entities.UserWeatherAlert
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WeatherAlertScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager,
) {

    fun scheduleAlert(alert: UserWeatherAlert) {
        when (alert.repetitionType) {
            RepetitionType.SINGLE -> scheduleSingleAlert(alert)
            RepetitionType.DAILY -> schedulePeriodicAlert(alert)
        }
    }

    fun cancelAlert(alertPreferences: UserWeatherAlert) {
        workManager.cancelWorkById(alertPreferences.id)
    }

    private fun scheduleSingleAlert(alert: UserWeatherAlert) {
        val uniqueWorkRequest: OneTimeWorkRequest = OneTimeWorkRequest.Builder(
            WeatherAlertsWorker::class.java,
        ).let { buildRequest(alert, it) }
        workManager.enqueueUniqueWork(
            alert.id.toString(),
            ExistingWorkPolicy.REPLACE,
            uniqueWorkRequest,
        )
    }

    private fun schedulePeriodicAlert(alert: UserWeatherAlert) {
        val periodicWorkRequest: PeriodicWorkRequest = PeriodicWorkRequest.Builder(
            WeatherAlertsWorker::class.java, 24, TimeUnit.HOURS
        ).let { buildRequest(alert, it) }
        workManager.enqueueUniquePeriodicWork(
            alert.id.toString(),
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest,
        )
    }

    private fun <W : WorkRequest, B : WorkRequest.Builder<B, W>> buildRequest(
        alert: UserWeatherAlert, request: B
    ): W {
        val startDateTime = alert.startDate.atTime(alert.time)
        val delay = Duration.between(LocalDateTime.now(), startDateTime)
        return request.setId(alert.id)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInitialDelay(delay)
            .build()
    }

}
