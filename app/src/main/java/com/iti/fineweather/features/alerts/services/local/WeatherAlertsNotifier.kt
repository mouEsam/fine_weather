package com.iti.fineweather.features.alerts.services.local

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ForegroundInfo
import com.iti.fineweather.R
import com.iti.fineweather.features.alerts.entities.UserWeatherAlert
import com.iti.fineweather.features.weather.models.WeatherAlert
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherAlertsNotifier @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
) {
    companion object {
        private const val CHANNEL_ID = "weather_alerts_notification_channel"
    }

    fun notifyForAlert(
        alert: WeatherAlert,
        alertPreferences: UserWeatherAlert,
    ) {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notifyForAlertImpl(alert, alertPreferences)
        }
    }

    fun notifyForNoAlerts(alertPreferences: UserWeatherAlert) {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notifyForNoAlertsImpl(alertPreferences)
        }
    }

    @SuppressLint("InlinedApi")
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun notifyForNoAlertsImpl(alertPreferences: UserWeatherAlert) {
        NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(applicationContext.resources.getString(R.string.app_name))
            .setContentText(applicationContext.resources.getString(R.string.alerts_no_weather_alerts))
            .setSilent(!alertPreferences.alarmEnabled)
            .build().also { notification ->
                NotificationManagerCompat
                    .from(applicationContext)
                    .notify(alertPreferences.hashCode(), notification)
            }
    }

    @SuppressLint("InlinedApi")
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun notifyForAlertImpl(
        alert: WeatherAlert,
        alertPreferences: UserWeatherAlert,
    ) {
        NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(alert.event)
            .setContentText(alert.description)
            .setSilent(!alertPreferences.alarmEnabled)
            .build().also { notification ->
                NotificationManagerCompat
                    .from(applicationContext)
                    .notify(alert.hashCode(), notification)
            }
    }

    fun createNotificationForegroundInfo(alertId: UUID): ForegroundInfo {
        createChannel()
        return ForegroundInfo(
            alertId.hashCode(),
            NotificationCompat
                .Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(applicationContext.resources.getString(R.string.alerts_notification_operation_title))
                .setContentText(applicationContext.resources.getString(R.string.alerts_notification_operation_text))
                .setSilent(true)
                .build()
        )
    }

    private fun createChannel() {
        NotificationChannelCompat
            .Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName(applicationContext.resources.getString(R.string.alerts_notification_operation_channel_desc))
            .setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setShowBadge(true)
            .build().apply {
                NotificationManagerCompat.from(applicationContext).createNotificationChannel(this)
            }
    }
}