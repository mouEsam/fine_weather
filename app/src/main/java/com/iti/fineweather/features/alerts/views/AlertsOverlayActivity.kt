package com.iti.fineweather.features.alerts.views

import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import com.iti.fineweather.BuildConfig
import com.iti.fineweather.core.theme.FineWeatherTheme
import com.iti.fineweather.features.alerts.services.local.WeatherAlertsNotifier
import com.iti.fineweather.features.weather.models.WeatherAlertView
import dagger.hilt.android.AndroidEntryPoint
import java.time.ZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class AlertsOverlayActivity : ComponentActivity() {

    companion object {
        const val ALERTS = "alerts"
    }

    @Inject
    lateinit var weatherAlertsNotifier: WeatherAlertsNotifier

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            this.setTurnScreenOn(true);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }

        val alerts = extractAlerts(intent)

        mediaPlayer = MediaPlayer.create(
            this,
            weatherAlertsNotifier.notificationUri,
        )
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                .build()
        )
        mediaPlayer.start()

        setContent {
            FineWeatherTheme {
                AlertsOverlay(alerts = alerts, onClose = { finishAndRemoveTask() })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
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
}
