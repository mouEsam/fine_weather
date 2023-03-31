package com.iti.fineweather.features.weather.views

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.iti.fineweather.core.navigation.LocalNavigation

@Composable
fun WeatherPage(modifier: Modifier = Modifier) {
    val navController = LocalNavigation.navController
    Button(onClick = {
        navController.navigateUp()
    }) {
        Text("Pop")
    }
}

