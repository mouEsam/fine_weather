package com.iti.fineweather.features.map.views

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.maps.android.compose.*
import com.iti.fineweather.core.helpers.CompositionScaffoldProvider
import com.iti.fineweather.core.helpers.LocalScaffold
import com.iti.fineweather.core.helpers.UiState
import com.iti.fineweather.core.navigation.LocalNavigation
import com.iti.fineweather.core.navigation.RouteInfo
import com.iti.fineweather.core.navigation.Screen
import com.iti.fineweather.core.theme.LocalAppTheme
import com.iti.fineweather.features.map.viewmodels.MapPlaceViewModel
import com.iti.fineweather.features.map.viewmodels.MapPlacesViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

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
fun MapScreen(
    mapPlaceViewModel: MapPlaceViewModel = hiltViewModel(),
) {
    val previousStackEntry = LocalNavigation.navController.previousBackStackEntry!!
    val currentBackStackEntry = LocalNavigation.backStackEntry
    val coroutineScope = rememberCoroutineScope()

    CompositionScaffoldProvider {
        Scaffold(
            scaffoldState = LocalScaffold.current,
        ) { innerPadding ->
            val uiSettings by remember { mutableStateOf(MapUiSettings()) }
            val properties by remember {
                mutableStateOf(MapProperties(mapType = MapType.SATELLITE))
            }
            var selectedLocation by rememberSaveable { mutableStateOf<LatLng?>(null) }
            val cameraPositionState = rememberCameraPositionState {}
            fun onLocation(location: LatLng) {
                selectedLocation = location
                coroutineScope.launch {
                    Timber.d("ANIMATING")
                    cameraPositionState.animate(
                        CameraUpdateFactory
                            .newCameraPosition(CameraPosition.fromLatLngZoom(location, 10f)),
                        durationMs = 500,
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                AutoCompletePlaces(
                    mapPlaceViewModel = mapPlaceViewModel,
                )
                Spacer(modifier = Modifier.height(LocalAppTheme.spaces.small))
                GoogleMap(
                    modifier = Modifier
                        .fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = properties,
                    uiSettings = uiSettings,
                    onMapClick = ::onLocation,
                ) {
                    val uiState by mapPlaceViewModel.uiState.collectAsState()
                    LaunchedEffect(key1 = uiState) {
                        when (uiState) {
                            is UiState.Error -> {
                                // TODO: show toast
                            }
                            is UiState.Loaded -> {
                                onLocation(uiState.data!!.location)
                            }
                            else -> {}
                        }
                    }
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

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun AutoCompletePlaces(
    mapPlaceViewModel: MapPlaceViewModel,
    mapPlacesViewModel: MapPlacesViewModel = hiltViewModel()
) {

    var textInput by rememberSaveable { mutableStateOf("") }
    var exp by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<AutocompletePrediction?>(null) }

    ExposedDropdownMenuBox(
        expanded = exp,
        onExpandedChange = {},
    ) {
        TextField(
            value = textInput,
            maxLines = 1,
            onValueChange = {
                textInput = it
                mapPlacesViewModel.getPredictions(it)
                exp = true
            },
            label = { Text("Search") }, // TODO: localize
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = exp,
                    onIconClick = {
                        exp = !exp
                    }
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .fillMaxWidth(1.0f)
                .onFocusChanged {
                    exp = it.hasFocus
                },
        )
        ExposedDropdownMenu(expanded = exp, onDismissRequest = { }) {
            val uiState by mapPlacesViewModel.uiState.collectAsState()
            when (uiState) {
                is UiState.Initial -> {}
                is UiState.Error -> {}
                is UiState.Loaded -> {
                    uiState.data!!.forEach { option ->
                        val label = option.getPrimaryText(null).toString()
                        DropdownMenuItem(
                            onClick = {
                                selectedOption = option
                                textInput = label
                                mapPlacesViewModel.getPredictions(label)
                                mapPlaceViewModel.getPlace(option)
                                exp = false
                            }
                        ) {
                            Text(text = label)
                        }
                    }
                }
                is UiState.Loading -> {

                }
            }
        }
    }
}
