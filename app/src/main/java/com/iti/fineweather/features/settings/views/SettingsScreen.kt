package com.iti.fineweather.features.settings.views

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.iti.fineweather.core.helpers.CompositionScaffoldProvider
import com.iti.fineweather.core.helpers.LocalScaffold
import com.iti.fineweather.core.navigation.RouteInfo
import com.iti.fineweather.core.navigation.Screen

object SettingsScreen: Screen<SettingsScreen.SettingsRoute> {

    override val routeInfo: SettingsRoute = SettingsRoute

    object SettingsRoute: RouteInfo {
        override val path: String = "weather"
        override val screen: @Composable () -> Unit = @Composable {
            WeatherScreen()
        }
    }
}

@Composable
@VisibleForTesting
fun WeatherScreen() {
    CompositionScaffoldProvider {
        Scaffold(
            scaffoldState = LocalScaffold.current,
        ) { innerPadding ->
            SettingsPage(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

