package com.iti.fineweather.features.alerts

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.iti.fineweather.BuildConfig
import com.iti.fineweather.R
import com.iti.fineweather.core.theme.FineWeatherTheme
import com.iti.fineweather.core.theme.LocalTheme
import com.iti.fineweather.features.alerts.services.local.WeatherAlertsNotifier
import com.iti.fineweather.features.common.utils.rememberLocalizedDateTimeFormatter
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

        val alerts = if (BuildConfig.DEBUG) {
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
                intent.getParcelableArrayListExtra(ALERTS, WeatherAlertView::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableArrayListExtra(ALERTS)
            }
        }

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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(LocalTheme.spaces.large),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    Surface(
                        color = LocalTheme.colors.main,
                        contentColor = LocalTheme.colors.mainContent,
                        shape = LocalTheme.shapes.largeRoundedCornerShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f),
                    ) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier
                                .padding(LocalTheme.spaces.small),
                        ) {
                            Box(
                                modifier = Modifier.weight(1.0f),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (!alerts.isNullOrEmpty()) {
                                    Alerts(
                                        contentColor = LocalTheme.colors.main,
                                        alerts = alerts,
                                    )
                                } else {
                                    Text(
                                        text = stringResource(R.string.alerts_no_weather_alerts),
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(LocalTheme.spaces.small))
                            ElevatedButton(
                                onClick = { finishAndRemoveTask() },
                            ) {
                                Text(
                                    text = stringResource(android.R.string.ok)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
    }
}

@Composable
fun Alerts(
    contentColor: Color,
    alerts: List<WeatherAlertView>?,
) {
    val dateTimeFormatter = rememberLocalizedDateTimeFormatter("MM-dd, EEEE hh:mm a")

    LazyColumn(
        contentPadding = PaddingValues(
            vertical = LocalTheme.spaces.medium,
            horizontal = LocalTheme.spaces.medium,
        ),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(
            space = LocalTheme.spaces.medium,
            alignment = Alignment.Top,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {

        items(count = alerts?.size ?: 7) { index ->
            val alert = alerts?.getOrNull(index)
            val color = LocalContentColor.current
            Surface(
                color = Color.Unspecified,
                contentColor = contentColor
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(
                        space = LocalTheme.spaces.small,
                        alignment = Alignment.Top,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            shape = LocalTheme.shapes.mediumRoundedCornerShape,
                        ).background(
                            color = color.copy(alpha = 0.8f)
                        ).padding(
                            vertical = LocalTheme.spaces.medium,
                            horizontal = LocalTheme.spaces.medium,
                        ),
                ) {
                    Text(
                        text = alert?.event ?: stringResource(R.string.placeholder_event),
                        style = LocalTheme.typography.action,
                    )
                    val startsAt =
                        alert?.start?.let(dateTimeFormatter::format) ?: stringResource(R.string.placeholder_event)
                    Text(
                        text = stringResource(R.string.home_tab_alert_start_at, startsAt),
                        style = LocalTheme.typography.body,
                    )
                    val endsAt =
                        alert?.end?.let(dateTimeFormatter::format) ?: stringResource(R.string.placeholder_event)
                    Text(
                        text = stringResource(R.string.home_tab_alert_until, endsAt),
                        style = LocalTheme.typography.body,
                    )
                    Text(
                        text = alert?.description ?: stringResource(R.string.placeholder_event_description),
                        style = LocalTheme.typography.label,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val source = alert?.senderName ?: stringResource(R.string.placeholder_event)
                    Text(
                        text = stringResource(R.string.home_tab_alert_source, source),
                        style = LocalTheme.typography.label,
                    )
                }
            }
        }
    }
}