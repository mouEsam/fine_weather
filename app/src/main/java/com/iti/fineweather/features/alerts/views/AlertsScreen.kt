package com.iti.fineweather.features.alerts.views

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.iti.fineweather.core.helpers.CompositionScaffoldProvider
import com.iti.fineweather.core.helpers.LocalScaffold
import com.iti.fineweather.core.navigation.RouteInfo
import com.iti.fineweather.core.navigation.Screen

object AlertsScreen: Screen<AlertsScreen.AlertsRoute> {

    override val routeInfo: AlertsRoute = AlertsRoute

    object AlertsRoute: RouteInfo {
        override val path: String = "alerts"
        override val screen: @Composable () -> Unit = @Composable {
            AlertsScreen()
        }
    }
}

@Composable
@VisibleForTesting
fun AlertsScreen() {
    CompositionScaffoldProvider {
        Scaffold(
            scaffoldState = LocalScaffold.current,
        ) { innerPadding ->
            AlertsPage(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

