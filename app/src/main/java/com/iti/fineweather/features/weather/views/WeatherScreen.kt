package com.iti.fineweather.features.weather.views

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import com.iti.fineweather.core.helpers.CompositionScaffoldProvider
import com.iti.fineweather.core.helpers.LocalScaffold
import com.iti.fineweather.core.navigation.LocalNavigation
import com.iti.fineweather.core.navigation.RouteArgument
import com.iti.fineweather.core.navigation.RouteInfo
import com.iti.fineweather.core.navigation.Screen
import com.iti.fineweather.features.home.helpers.BarNavigationItem

object WeatherScreen: Screen<WeatherScreen.WeatherRoute> {

    override val routeInfo: WeatherRoute = WeatherRoute

    object WeatherRoute: RouteInfo {
        override val path: String = "weather"
        override val args: List<RouteArgument<*>> = listOf(RouteArgument(
            name = "location",
            dataType = NavType.StringType,
        ))
        override val screen: @Composable () -> Unit = @Composable {
            WeatherScreen()
        }
    }
}

private val items = listOf(
    BarNavigationItem.Home,
    BarNavigationItem.Bookmarks,
)

@Composable
@VisibleForTesting
fun WeatherScreen() {
    LocalNavigation.navController
    val currentBackStackEntry = LocalNavigation.backStackEntry
    CompositionScaffoldProvider {
        Scaffold(
            scaffoldState = LocalScaffold.current,
        ) { innerPadding ->
            WeatherPage(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
