package com.iti.fineweather.features.alerts.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.iti.fineweather.features.alerts.viewmodels.WeatherAlertsViewModel

@Composable
fun AlertsPage(
    modifier: Modifier = Modifier,
    alertsViewModel: WeatherAlertsViewModel = hiltViewModel<WeatherAlertsViewModel>()
) {

}

