package com.iti.fineweather.features.alerts.services.local

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ForegroundInfo
import com.iti.fineweather.R
import com.iti.fineweather.features.alerts.entities.UserWeatherAlert
import com.iti.fineweather.features.alerts.helpers.OverlayWindow
import com.iti.fineweather.features.alerts.views.AlertsOverlayActivity
import com.iti.fineweather.features.common.helpers.Constants
import com.iti.fineweather.features.weather.helpers.WeatherDataMapper
import com.iti.fineweather.features.weather.models.WeatherAlert
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherAlertsNotifier @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val weatherDataMapper: WeatherDataMapper,
    private val overlayWindow: OverlayWindow,
) {
    companion object {
        private const val CHANNEL_ID = "weather_alerts_notification_channel"
    }

    val notificationUri: Uri
        get() {
            return (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManagerCompat.from(applicationContext).getNotificationChannel(CHANNEL_ID)?.sound
            } else { null }) ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }

    fun notifyForAlert(
        alerts: List<WeatherAlert>,
        alertPreferences: UserWeatherAlert,
    ) {
        if (alertPreferences.alarmEnabled) {
            notifyForAlertsOverlayImpl(alerts, alertPreferences)
        } else {
            notifyForAlertsNotificationImpl(alerts, alertPreferences)
        }
    }

    private fun notifyForAlertsOverlayImpl(
        alerts: List<WeatherAlert>,
        alertPreferences: UserWeatherAlert,
    ) {
        if (Constants.USE_OVERLAY_ACTIVITY) {
            applicationContext.startActivity(Intent(applicationContext, AlertsOverlayActivity::class.java).apply {
                putExtra(AlertsOverlayActivity.ALERTS, ArrayList(weatherDataMapper.mapAlerts(alerts)))
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } else {
            applicationContext.startService(Intent(applicationContext, OverlayWindowService::class.java).apply {
                putExtra(OverlayWindowService.ALERTS, ArrayList(weatherDataMapper.mapAlerts(alerts)))
            })
        }
    }

    private fun notifyForAlertsNotificationImpl(
        alerts: List<WeatherAlert>,
        alertPreferences: UserWeatherAlert,
    ) {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (alerts.isNotEmpty()) {
                alerts.forEach { alert ->
                    notifyForAlertImpl(alert, alertPreferences)
                }
            } else {
                notifyForNoAlertsNotificationImpl(alertPreferences)
            }
        }
    }

    @SuppressLint("InlinedApi")
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun notifyForNoAlertsNotificationImpl(alertPreferences: UserWeatherAlert) {
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