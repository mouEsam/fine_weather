package com.iti.fineweather.features.map.views

import android.Manifest
import android.annotation.SuppressLint
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.MutableLiveData
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.maps.android.compose.*
import com.iti.fineweather.R
import com.iti.fineweather.core.helpers.CompositionScaffoldProvider
import com.iti.fineweather.core.helpers.LocalScaffold
import com.iti.fineweather.core.helpers.UiState
import com.iti.fineweather.core.navigation.LocalNavigation
import com.iti.fineweather.core.navigation.RouteInfo
import com.iti.fineweather.core.navigation.Screen
import com.iti.fineweather.core.theme.LocalTheme
import com.iti.fineweather.features.common.views.showErrorSnackbar
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

@OptIn(ExperimentalComposeUiApi::class, ExperimentalPermissionsApi::class)
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
            val snackBarState = LocalScaffold.snackbarHost
            val noGpsError = stringResource(R.string.error_location_permission)
            val permissions = rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            ) { permissions ->
                @SuppressLint("MissingPermission")
                if (permissions.none { p -> p.value }) {
                    coroutineScope.launch {
                        snackBarState.showSnackbar(noGpsError)
                    }
                }
            }

            val hasLocationAccess by remember { derivedStateOf { permissions.permissions.any { it.status == PermissionStatus.Granted } } }

            val uiSettings by remember { mutableStateOf(MapUiSettings()) }
            var properties by remember {
                mutableStateOf(
                    MapProperties(
                        mapType = MapType.NORMAL,
                        isMyLocationEnabled = hasLocationAccess,
                    )
                )
            }
            var selectedLocation by rememberSaveable { mutableStateOf<MapPlaceResult?>(null) }
            val cameraPositionState = rememberCameraPositionState {}

            LaunchedEffect(key1 = hasLocationAccess) {
                properties = properties.copy(isMyLocationEnabled = hasLocationAccess)
            }

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
                            .newCameraPosition(
                                CameraPosition.fromLatLngZoom(
                                    location.location,
                                    10f
                                )
                            ),
                        durationMs = 500, // TODO: extract
                    )
                }
            }

            fun onMapLocation(latLng: LatLng) {
                mapPlaceViewModel.getPlace(latLng)
                keyboard?.hide()
                focusManager.clearFocus()
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
                    showErrorSnackbar(uiState)
                    LaunchedEffect(key1 = uiState) {
                        when (uiState) {
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
                        onMapClick = ::onMapLocation,
                        onMyLocationClick = { location ->
                            val latLng = LatLng(location.latitude, location.longitude)
                            onMapLocation(latLng)
                        }
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
                    if (!hasLocationAccess)
                        IconButton(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(LocalTheme.spaces.large),
                            onClick = {
                                permissions.launchMultiplePermissionRequest()
                            },
                            colors = IconButtonDefaults.filledIconButtonColors()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MyLocation,
                                contentDescription = null,
                            )
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
                        Text(stringResource(R.string.map_select_place, selectedLocation.name))
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
            label = { Text(stringResource(R.string.map_search)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = exp
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor()
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
