package com.iti.fineweather.features.weather.views

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import com.iti.fineweather.core.helpers.CompositionScaffoldProvider
import com.iti.fineweather.core.helpers.LocalScaffold
import com.iti.fineweather.core.navigation.LocalNavigation
import com.iti.fineweather.core.navigation.RouteArgument
import com.iti.fineweather.core.navigation.RouteInfo
import com.iti.fineweather.core.navigation.Screen
import com.iti.fineweather.core.utils.DoubleType
import com.iti.fineweather.features.weather.models.WeatherLocation

object WeatherScreen : Screen<WeatherScreen.WeatherRoute> {

    override val routeInfo: WeatherRoute = WeatherRoute

    object WeatherRoute : RouteInfo {
        override val path: String = "weather"
        val name = RouteArgument(
            name = "name",
            dataType = NavType.StringType,
        )
        val city = RouteArgument(
            name = "city",
            dataType = NavType.StringType,
        )
        val latitude = RouteArgument(
            name = "latitude",
            dataType = NavType.DoubleType,
        )
        val longitude = RouteArgument(
            name = "longitude",
            dataType = NavType.DoubleType,
        )
        override val args: List<RouteArgument<*>> = listOf(name, city, latitude, longitude)
        override val screen: @Composable () -> Unit = @Composable {
            WeatherScreen()
        }
    }
}

@Composable
@VisibleForTesting
fun WeatherScreen() {
    val currentBackStackEntry = LocalNavigation.backStackEntry
    val weatherLocation by remember {
        val name = WeatherScreen.routeInfo.name.require(currentBackStackEntry)
        val city = WeatherScreen.routeInfo.city.require(currentBackStackEntry)
        val latitude = WeatherScreen.routeInfo.latitude.require(currentBackStackEntry)
        val longitude = WeatherScreen.routeInfo.longitude.require(currentBackStackEntry)
        mutableStateOf(WeatherLocation(name, city, latitude, longitude))
    }
    CompositionScaffoldProvider {
        Scaffold(
            scaffoldState = LocalScaffold.current,
        ) { innerPadding ->
            WeatherPage(
                modifier = Modifier
                    .padding(innerPadding)
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                weatherLocation = weatherLocation,
            )
        }
    }
}

