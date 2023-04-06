package com.iti.fineweather.features.alerts.views

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.iti.fineweather.R
import com.iti.fineweather.core.helpers.CompositionScaffoldProvider
import com.iti.fineweather.core.helpers.LocalScaffold
import com.iti.fineweather.core.navigation.RouteInfo
import com.iti.fineweather.core.navigation.Screen
import com.iti.fineweather.core.theme.LocalTheme

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
            topBar = {
                val color = LocalTheme.colors.main
                 Surface(
                     color = color,
                     contentColor = LocalTheme.colors.mainContent,
                     modifier = Modifier
                         .background(color)
                         .statusBarsPadding()
                 ) {
                     TopAppBar(
                         backgroundColor = color,
                         contentColor = LocalTheme.colors.mainContent,
                     ) {
                         Text(
                             modifier = Modifier.fillMaxWidth(),
                             text = stringResource(R.string.alerts_title),
                             style = LocalTheme.typography.title,
                             textAlign = TextAlign.Center,
                         )
                     }
                 }
            },
        ) { innerPadding ->
            val color = LocalTheme.colors.main
            Surface(
                color = color,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = color),
                contentColor = LocalTheme.colors.mainContent,
            ) {
                AlertsPage(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

