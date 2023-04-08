package com.iti.fineweather.features.alerts.services.local

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.iti.fineweather.BuildConfig
import com.iti.fineweather.core.theme.FineWeatherTheme
import com.iti.fineweather.features.alerts.helpers.OverlayWindow
import com.iti.fineweather.features.alerts.views.AlertsOverlay
import com.iti.fineweather.features.alerts.views.AlertsOverlayActivity
import com.iti.fineweather.features.weather.models.WeatherAlertView
import dagger.hilt.android.AndroidEntryPoint
import java.time.ZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class OverlayWindowService: Service() {

    companion object {
        const val ALERTS = "alerts"
    }

    @Inject
    lateinit var overlayWindow: OverlayWindow
    @Inject
    lateinit var weatherAlertsNotifier: WeatherAlertsNotifier

    private lateinit var mediaPlayer: MediaPlayer

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(
            this.applicationContext,
            weatherAlertsNotifier.notificationUri,
        )
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                .build()
        )
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val alerts = extractAlerts(intent)
        mediaPlayer.start()
        overlayWindow.open {
            FineWeatherTheme {
                AlertsOverlay(
                    alerts = alerts,
                    onClose = { stopSelf(startId) },
                )
            }
        }
        return Service.START_STICKY
    }

    private fun extractAlerts(intent: Intent): List<WeatherAlertView>? {
        return if (BuildConfig.DEBUG) {
            (0..10).map {
                WeatherAlertView(
                    senderName = "Sender Name",
                    event = "Tsunami",
                    start = ZonedDateTime.now(),
                    end = ZonedDateTime.now(),
                    description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop."
                )
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableArrayListExtra(AlertsOverlayActivity.ALERTS, WeatherAlertView::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableArrayListExtra(AlertsOverlayActivity.ALERTS)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayWindow.close()
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}
