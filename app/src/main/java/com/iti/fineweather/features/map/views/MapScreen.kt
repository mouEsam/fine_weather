package com.iti.fineweather.features.map.views

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.maps.android.compose.*
import com.iti.fineweather.core.helpers.CompositionScaffoldProvider
import com.iti.fineweather.core.helpers.LocalScaffold
import com.iti.fineweather.core.helpers.UiState
import com.iti.fineweather.core.navigation.LocalNavigation
import com.iti.fineweather.core.navigation.RouteInfo
import com.iti.fineweather.core.navigation.Screen
import com.iti.fineweather.core.theme.LocalTheme
import com.iti.fineweather.features.map.models.MapPlaceResult
import com.iti.fineweather.features.map.viewmodels.MapPlaceViewModel
import com.iti.fineweather.features.map.viewmodels.MapPlacesViewModel
import kotlinx.coroutines.launch

object MapScreen : Screen<MapScreen.MapRoute> {

    override val routeInfo: MapRoute = MapRoute
    const val RESULT_KEY = "map_selection_result"

    object MapRoute : RouteInfo {
        override val path: String = "map"
        override val screen: @Composable () -> Unit = @Composable {
            MapScreen()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@VisibleForTesting
fun MapScreen(
    mapPlaceViewModel: MapPlaceViewModel = hiltViewModel(),
) {

    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val navController = LocalNavigation.navController
    val previousStackEntry = navController.previousBackStackEntry!!
    val resultLiveData: MutableLiveData<MapPlaceResult?> = previousStackEntry
        .savedStateHandle
        .getLiveData(MapScreen.RESULT_KEY)

    val coroutineScope = rememberCoroutineScope()

    CompositionScaffoldProvider {
        Scaffold(
            containerColor = LocalTheme.colors.main,
            contentColor = LocalTheme.colors.mainContent,
            snackbarHost = { SnackbarHost(LocalScaffold.snackbarHost) },
        ) { innerPadding ->
            val uiSettings by remember { mutableStateOf(MapUiSettings()) }
            val properties by remember {
                mutableStateOf(MapProperties(mapType = MapType.SATELLITE))
            }
            var selectedLocation by rememberSaveable { mutableStateOf<MapPlaceResult?>(null) }
            val cameraPositionState = rememberCameraPositionState {}

            DisposableEffect(key1 = true) {
                onDispose {
                    if (selectedLocation == null) {
                        resultLiveData.value = null
                    }
                }
            }

            fun onLocation(location: MapPlaceResult) {
                selectedLocation = location
                coroutineScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory
                            .newCameraPosition(CameraPosition.fromLatLngZoom(location.location, 10f)),
                        durationMs = 500, // TODO: extract
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .statusBarsPadding()
            ) {
                AutoCompletePlaces(
                    mapPlaceViewModel = mapPlaceViewModel,
                    modifier = Modifier.padding(horizontal = LocalTheme.spaces.small),
                )
                Spacer(modifier = Modifier.height(LocalTheme.spaces.small))
                Box(
                    modifier = Modifier
                        .weight(1.0f),
                    contentAlignment = Alignment.Center,
                ) {
                    val uiState by mapPlaceViewModel.uiState.collectAsState()
                    LaunchedEffect(key1 = uiState) {
                        when (uiState) {
                            is UiState.Error -> {
                                // TODO: show toast
                            }

                            is UiState.Loaded -> {
                                onLocation(uiState.data!!)
                            }

                            else -> {}
                        }
                    }
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxHeight(1.0f)
                            .fillMaxWidth(),
                        cameraPositionState = cameraPositionState,
                        properties = properties,
                        uiSettings = uiSettings,
                        onMapClick = { latLng ->
                            mapPlaceViewModel.getPlace(latLng)
                            keyboard?.hide()
                            focusManager.clearFocus()
                        },
                    ) {
                        selectedLocation?.let { location ->
                            Marker(
                                state = MarkerState(
                                    position = location.location,
                                ),
                                title = location.name,
                            )
                        }
                    }
                    when (uiState) {
                        is UiState.Loading -> {
                            CircularProgressIndicator()
                        }

                        else -> {}
                    }
                }
                selectedLocation?.let { selectedLocation ->
                    Spacer(modifier = Modifier.height(LocalTheme.spaces.small))
                    ElevatedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = LocalTheme.spaces.small),
                        onClick = {
                            navController.navigateUp()
                            resultLiveData.value = selectedLocation
                        },
                    ) {
                        Text("Select") // TODO: Localize
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@VisibleForTesting
fun AutoCompletePlaces(
    mapPlaceViewModel: MapPlaceViewModel,
    mapPlacesViewModel: MapPlacesViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {

    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    var textInput by rememberSaveable { mutableStateOf("") }
    var exp by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<AutocompletePrediction?>(null) }

    ExposedDropdownMenuBox(
        expanded = exp,
        onExpandedChange = {},
        modifier = modifier,
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
                    expanded = exp
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
                            text = { Text(text = label) },
                            onClick = {
                                selectedOption = option
                                textInput = label
                                keyboard?.hide()
                                focusManager.clearFocus()
                                mapPlacesViewModel.getPredictions(label)
                                mapPlaceViewModel.getPlace(option)
                                exp = false
                            }
                        )
                    }
                }

                is UiState.Loading -> {
                }
            }

        }

    }
}
