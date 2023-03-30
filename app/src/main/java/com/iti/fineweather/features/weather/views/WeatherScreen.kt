package com.iti.fineweather.features.weather.views

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.iti.fineweather.core.helpers.CompositionScaffoldProvider
import com.iti.fineweather.core.helpers.LocalScaffold
import com.iti.fineweather.core.navigation.RouteArgument
import com.iti.fineweather.core.navigation.RouteInfo
import com.iti.fineweather.core.navigation.Screen
import timber.log.Timber

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

@Composable
@VisibleForTesting
fun WeatherScreen() {
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

