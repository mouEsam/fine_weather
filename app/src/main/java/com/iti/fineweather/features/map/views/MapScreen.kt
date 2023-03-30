package com.iti.fineweather.features.map.views

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.iti.fineweather.core.helpers.CompositionScaffoldProvider
import com.iti.fineweather.core.helpers.LocalScaffold
import com.iti.fineweather.core.navigation.LocalNavigation
import com.iti.fineweather.core.navigation.RouteInfo
import com.iti.fineweather.core.navigation.Screen

object MapScreen: Screen<MapScreen.MapRoute> {

    override val routeInfo: MapRoute = MapRoute

    object MapRoute: RouteInfo {
        override val path: String = "map"
        override val screen: @Composable () -> Unit = @Composable {
            MapScreen()
        }
    }
}

@Composable
@VisibleForTesting
fun MapScreen() {
    val previousStackEntry = LocalNavigation.navController
    val currentBackStackEntry = LocalNavigation.backStackEntry
    CompositionScaffoldProvider {
        Scaffold(
            scaffoldState = LocalScaffold.current,
        ) { innerPadding ->
            var uiSettings by remember { mutableStateOf(MapUiSettings()) }
            var properties by remember {
                mutableStateOf(MapProperties(mapType = MapType.SATELLITE))
            }
            var selectedLocation by rememberSaveable { mutableStateOf<LatLng?>(null) }
            val cameraPositionState = rememberCameraPositionState {}
            Box(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                GoogleMap(
                    modifier = Modifier
                        .fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = properties,
                    uiSettings = uiSettings,
                    onMapClick = { location -> selectedLocation = location }
                ) {
                    selectedLocation?.let {  location ->
                        Marker(
                            state = MarkerState(position = location),
                        )
                    }
                }
            }
        }
    }
}
