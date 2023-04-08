package com.iti.fineweather.features.settings.views

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.iti.fineweather.R
import com.iti.fineweather.core.helpers.LocalScaffold
import com.iti.fineweather.core.helpers.UiState
import com.iti.fineweather.core.navigation.LocalNavigation
import com.iti.fineweather.core.theme.LocalTheme
import com.iti.fineweather.core.utils.getResult
import com.iti.fineweather.core.utils.navigate
import com.iti.fineweather.features.common.views.showErrorSnackbar
import com.iti.fineweather.features.map.models.MapPlaceResult
import com.iti.fineweather.features.map.views.MapScreen
import com.iti.fineweather.features.settings.models.UserPreferences
import com.iti.fineweather.features.settings.utils.toLocale
import com.iti.fineweather.features.settings.utils.toLocalizedName
import com.iti.fineweather.features.settings.viewmodels.SettingsViewModel
import kotlinx.coroutines.launch
import timber.log.Timber


@Composable
fun SettingsPage(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {

    val uiState by settingsViewModel.uiState.collectAsState()
    showErrorSnackbar(uiState)
    newSettingsContent(settingsViewModel)

    SettingsContent(
        modifier = modifier,
        settingsViewModel = settingsViewModel,
        settingsUiState = uiState,
    )
}

@Composable
fun newSettingsContent(
    settingsViewModel: SettingsViewModel,
) {
    val uiState by settingsViewModel.operationState.collectAsState(UiState.Initial())
    showErrorSnackbar(uiState)
}

@Composable
fun SettingsContent(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel,
    settingsUiState: UiState<UserPreferences> = UiState.Initial(),
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (settingsUiState) {
            is UiState.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )

            else -> SettingsList(
                settings = settingsUiState.data,
                settingsViewModel = settingsViewModel,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)
@Composable
fun SettingsList(
    settings: UserPreferences?,
    settingsViewModel: SettingsViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarState = LocalScaffold.snackbarHost
    val noGpsError = stringResource(R.string.error_location_permission)
    val permissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    ) { permissions ->
        @SuppressLint("MissingPermission")
        if (permissions.any { p -> p.value }) {
            settingsViewModel.updateGpsLocation()
        } else {
            coroutineScope.launch {
                snackbarState.showSnackbar(noGpsError)
            }
        }
    }

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = LocalTheme.spaces.large)
            .padding(top = LocalTheme.spaces.large)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(
            space = LocalTheme.spaces.large,
        )
    ) {

        val lifecycleOwner = LocalLifecycleOwner.current
        val currentStackEntry = LocalNavigation.backStackEntry
        val navController = LocalNavigation.navController
        val configs = LocalConfiguration.current

        Configuration(
            label = stringResource(R.string.settings_language),
            options = UserPreferences.Language.values().take(UserPreferences.Language.values().size - 1),
            optionRenderer = {
                ConfigurationValue(label = it.toLocalizedName())
            },
            onOptionSelected = {
                configs.setLocale(it.toLocale())
                settingsViewModel.updateLanguage(it)
            },
            selectedOption = settings?.language
        )
        Configuration(
            label = stringResource(R.string.settings_location_source),
            subLabel = settings?.location?.city,
            options = UserPreferences.LocationType.values().take(UserPreferences.LocationType.values().size - 1),
            optionRenderer = {
                ConfigurationValue(label = it.toLocalizedName())
            },
            onOptionSelected = {
                when (it) {
                    UserPreferences.LocationType.MAP -> {
                        navController.navigate(MapScreen.routeInfo.toNavRequest())
                        lifecycleOwner.lifecycleScope.launch {
                            val locationResult = currentStackEntry.getResult<MapPlaceResult>(MapScreen.RESULT_KEY)
                            if (locationResult != null) {
                                Timber.d("GOT $locationResult")
                                settingsViewModel.updateMapLocation(locationResult)
                            }
                        }
                    }

                    UserPreferences.LocationType.GPS -> {
                        permissions.launchMultiplePermissionRequest()
                    }

                    else -> {}
                }
            },
            selectedOption = settings?.locationType
        )
        Configuration(
            label = stringResource(R.string.settings_temperature_unit),
            options = UserPreferences.TemperatureUnit.values().take(UserPreferences.TemperatureUnit.values().size - 1),
            optionRenderer = {
                ConfigurationValue(label = it.toLocalizedName())
            },
            onOptionSelected = {
                settingsViewModel.updateTemperatureUnit(it)
            },
            selectedOption = settings?.temperatureUnit
        )
        Configuration(
            label = stringResource(R.string.settings_wind_speed_unit),
            options = UserPreferences.WindSpeedUnit.values().take(UserPreferences.WindSpeedUnit.values().size - 1),
            optionRenderer = {
                ConfigurationValue(label = it.toLocalizedName())
            },
            onOptionSelected = {
                settingsViewModel.updateWindSpeedUnit(it)
            },
            selectedOption = settings?.windSpeedUnit
        )

        Spacer(modifier = Modifier.padding(bottom = LocalTheme.spaces.large).navigationBarsPadding())
    }
}

@Composable
fun <T> Configuration(
    label: String,
    subLabel: String? = null,
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
    optionRenderer: @Composable (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                shape = LocalTheme.shapes.largeRoundedCornerShape,
            ).background(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Unspecified,
                        LocalTheme.colors.mainContent,
                    ),
                    startX = -100f,
                )
            ).clickable {
                expanded = true
            }.padding(
                vertical = LocalTheme.spaces.medium,
                horizontal = LocalTheme.spaces.large,
            ),
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = label,
                color = LocalTheme.colors.mainContent,
                style = LocalTheme.typography.bodyBold,
            )

            if (subLabel != null) {
                Text(
                    text = subLabel,
                    color = LocalTheme.colors.mainContent,
                    style = LocalTheme.typography.body,
                )
            }
        }

        TextButton(
            onClick = {
                expanded = true
            },
            colors = ButtonDefaults.textButtonColors(
                contentColor = LocalTheme.colors.main,
            ),
        ) {
            Box(
                modifier = Modifier.defaultMinSize(minWidth = LocalTheme.spaces.xxxLarge),
            ) {
                selectedOption?.let { optionRenderer(it) }
            }
            Spacer(modifier = Modifier.width(LocalTheme.spaces.medium))
            Icon(
                painter = painterResource(R.drawable.ic_edit),
                contentDescription = null,
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                for (option in options) {
                    DropdownMenuItem(
                        text = {
                            optionRenderer(option)
                        },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfigurationValue(label: String) {
    Text(
        text = label,
        color = LocalTheme.colors.main,
        style = LocalTheme.typography.bodyBold,
    )
}

