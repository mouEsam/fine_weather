package com.iti.fineweather.features.home.views

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.iti.fineweather.core.helpers.CompositionScaffoldProvider
import com.iti.fineweather.core.helpers.LocalScaffold
import com.iti.fineweather.core.navigation.RouteInfo
import com.iti.fineweather.core.navigation.Screen
import com.iti.fineweather.features.weather.views.WeatherPage

object HomeScreen: Screen<HomeScreen.HomeRoute> {

    override val routeInfo: HomeRoute = HomeRoute

    object HomeRoute: RouteInfo {
        override val path: String = "home"
        override val screen: @Composable () -> Unit = @Composable {
            HomeScreen()
        }
    }
}

@Composable
@VisibleForTesting
fun HomeScreen() {
    CompositionScaffoldProvider {
        Scaffold(
            snackbarHost = { SnackbarHost(LocalScaffold.snackbarHost) },
        ) { innerPadding ->
            WeatherPage(
                modifier = Modifier
                    .padding(innerPadding),
            )
        }
    }
}

