package com.iti.fineweather.core.controllers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavType
import com.iti.fineweather.core.navigation.AppNavigation
import com.iti.fineweather.core.navigation.LocalNavigation
import com.iti.fineweather.core.navigation.RouteArgument
import com.iti.fineweather.core.navigation.SimpleRouteInfo
import com.iti.fineweather.core.theme.FineWeatherTheme
import com.iti.fineweather.core.utils.navigate
import com.iti.fineweather.features.alerts.views.AlertsScreen
import com.iti.fineweather.features.home.views.HomeScreen
import com.iti.fineweather.features.map.views.MapScreen
import com.iti.fineweather.features.settings.views.SettingsScreen
import com.iti.fineweather.features.weather.services.remote.WeatherRemoteService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private val routes = listOf(
    SimpleRouteInfo(
        path = "test",
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Greeting("Android")
        }
    },
    SimpleRouteInfo(
        path = "test1",
        args = listOf(RouteArgument("i", dataType = NavType.IntType, defaultValue = 0))
    ) {
        SecondGreeting()
    },
    HomeScreen.routeInfo,
    MapScreen.routeInfo,
    SettingsScreen.routeInfo,
    AlertsScreen.routeInfo,
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var weatherRemoteService: WeatherRemoteService

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                val weather = weatherRemoteService.getWeather(
//                    latitude = 39.10622,
//                    longitude = -95.7230867,
//                )
//                Timber.d(weather.toString())
            }
        }
        setContent {
            FineWeatherTheme {
                // A surface container using the 'background' color from the theme
                AppNavigation(
                    routes = routes
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    val navController = LocalNavigation.navController
    Column {
        Text(text = "Hello $name!")
        Button(onClick = {
            Timber.d(HomeScreen.HomeRoute.toRoute())
            navController.navigate(HomeScreen.HomeRoute.toNavRequest())
        }) {
            Text("Navigate")
        }
    }
}

@Composable
fun SecondGreeting() {
    Text(text = "Hello! ${routes.last().args.first().get()}")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FineWeatherTheme {
        Greeting("Android")
    }
}