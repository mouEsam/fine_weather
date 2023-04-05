package com.iti.fineweather.core.controllers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.iti.fineweather.core.navigation.AppNavigation
import com.iti.fineweather.core.theme.FineWeatherTheme
import com.iti.fineweather.features.alerts.views.AlertsScreen
import com.iti.fineweather.features.bookmarks.views.BookmarksScreen
import com.iti.fineweather.features.home.views.HomeScreen
import com.iti.fineweather.features.map.views.MapScreen
import com.iti.fineweather.features.settings.views.SettingsScreen
import com.iti.fineweather.features.splash.views.SplashScreen
import com.iti.fineweather.features.weather.services.remote.WeatherRemoteService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private val routes = listOf(
    SplashScreen.routeInfo,
    HomeScreen.routeInfo,
    MapScreen.routeInfo,
    SettingsScreen.routeInfo,
    AlertsScreen.routeInfo,
    BookmarksScreen.routeInfo,
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var weatherRemoteService: WeatherRemoteService

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            FineWeatherTheme {
                AppNavigation(
                    routes = routes
                )
            }
        }
    }
}
