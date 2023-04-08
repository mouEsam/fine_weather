package com.iti.fineweather.core.controllers

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.iti.fineweather.core.navigation.AppNavigation
import com.iti.fineweather.core.theme.FineWeatherTheme
import com.iti.fineweather.core.utils.AppLayoutDirection
import com.iti.fineweather.features.alerts.views.AlertsScreen
import com.iti.fineweather.features.bookmarks.views.BookmarksScreen
import com.iti.fineweather.features.common.views.LockScreenOrientation
import com.iti.fineweather.features.home.views.HomeScreen
import com.iti.fineweather.features.map.views.MapScreen
import com.iti.fineweather.features.settings.views.SettingsScreen
import com.iti.fineweather.features.splash.views.SplashScreen
import com.iti.fineweather.features.weather.services.remote.WeatherRemoteService
import com.iti.fineweather.features.weather.views.WeatherScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var weatherRemoteService: WeatherRemoteService

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            FineWeatherTheme {
                AppLayoutDirection {
                    AppNavigation(
                        routes = listOf(
                            SplashScreen.routeInfo,
                            HomeScreen.routeInfo,
                            SettingsScreen.routeInfo,
                            BookmarksScreen.routeInfo,
                            AlertsScreen.routeInfo,
                            WeatherScreen.routeInfo,
                            MapScreen.routeInfo,
                        )
                    )
                }
            }
        }
    }
}
