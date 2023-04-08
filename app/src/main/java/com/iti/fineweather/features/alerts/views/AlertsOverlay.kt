package com.iti.fineweather.features.alerts.views

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.iti.fineweather.R
import com.iti.fineweather.core.theme.LocalTheme
import com.iti.fineweather.features.common.utils.rememberLocalizedDateTimeFormatter
import com.iti.fineweather.features.weather.models.WeatherAlertView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AlertsOverlay(
    alerts: List<WeatherAlertView>?,
    onClose: () -> Unit,
) {
    val animationDuration = 1000
    val coroutineScope = rememberCoroutineScope()
    var visible: Boolean by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalTheme.spaces.large),
        contentAlignment = Alignment.TopCenter,
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(animationSpec = tween(durationMillis = animationDuration)) { fullHeight ->
                -fullHeight / 2
            } + fadeIn(
                animationSpec = tween(durationMillis = animationDuration)
            ),
            exit = slideOutVertically(animationSpec = spring(stiffness = Spring.StiffnessHigh)) { fullHeight ->
                -fullHeight / 2
            } + fadeOut()
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
                        onClick = {
                            visible = false
                            coroutineScope.launch {
                                withContext(Dispatchers.Default) {
                                    delay(animationDuration.toLong())
                                }
                                onClose()
                            }
                        },
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
                        alert?.start?.let(dateTimeFormatter::format)
                            ?: stringResource(R.string.placeholder_event)
                    Text(
                        text = stringResource(R.string.home_tab_alert_start_at, startsAt),
                        style = LocalTheme.typography.body,
                    )
                    val endsAt =
                        alert?.end?.let(dateTimeFormatter::format)
                            ?: stringResource(R.string.placeholder_event)
                    Text(
                        text = stringResource(R.string.home_tab_alert_until, endsAt),
                        style = LocalTheme.typography.body,
                    )
                    Text(
                        text = alert?.description
                            ?: stringResource(R.string.placeholder_event_description),
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