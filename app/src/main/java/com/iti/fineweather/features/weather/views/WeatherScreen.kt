package com.iti.fineweather.features.weather.views

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import com.iti.fineweather.core.helpers.CompositionScaffoldProvider
import com.iti.fineweather.core.helpers.LocalScaffold
import com.iti.fineweather.core.navigation.*
import com.iti.fineweather.core.utils.DoubleType
import com.iti.fineweather.features.bookmarks.entities.PlaceBookmark
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
            defaultValue = 0.0,
        )
        val longitude = RouteArgument(
            name = "longitude",
            dataType = NavType.DoubleType,
            defaultValue = 0.0,
        )
        override val args: List<RouteArgument<*>> = listOf(name, city, latitude, longitude)
        override val screen: @Composable () -> Unit = @Composable {
            WeatherScreen()
        }
        fun toNavReq(location: PlaceBookmark): NavRequest {
            return toNavRequest(
                mapOf(
                    name to location.name,
                    city to location.city,
                    latitude to location.latitude,
                    longitude to location.longitude,
                )
            )
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
            snackbarHost = { SnackbarHost(LocalScaffold.snackbarHost) },
        ) { innerPadding ->
            WeatherPage(
                modifier = Modifier
                    .padding(innerPadding),
                showControls = false,
                weatherLocation = weatherLocation,
            )
        }
    }
}

