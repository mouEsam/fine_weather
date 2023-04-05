package com.iti.fineweather.features.weather.views

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.iti.fineweather.core.navigation.LocalNavigation
import com.iti.fineweather.features.weather.models.WeatherLocation
import com.iti.fineweather.features.weather.viewmodels.WeatherViewModel
import timber.log.Timber

@Composable
fun WeatherPage(
    modifier: Modifier = Modifier,
    weatherLocation: WeatherLocation? = null,
    weatherViewModel: WeatherViewModel = hiltViewModel(key = weatherLocation?.toString())
) {
    LaunchedEffect(key1 = weatherLocation) {
        weatherViewModel.getWeatherData(weatherLocation)
    }

    val uiState by weatherViewModel.uiState.collectAsState()
    LaunchedEffect(key1 = uiState) {
        Timber.d(uiState.toString())
    }

    val navController = LocalNavigation.navController
    Button(onClick = {
        navController.navigateUp()
    }) {
        Text("Pop")
    }
}

