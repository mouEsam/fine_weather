package com.iti.fineweather.features.splash.views

import android.Manifest
import android.annotation.SuppressLint
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.iti.fineweather.core.helpers.CompositionScaffoldProvider
import com.iti.fineweather.core.helpers.LocalScaffold
import com.iti.fineweather.core.helpers.UiState
import com.iti.fineweather.core.navigation.LocalNavigation
import com.iti.fineweather.core.navigation.RouteInfo
import com.iti.fineweather.core.navigation.Screen
import com.iti.fineweather.core.theme.LocalAppTheme
import com.iti.fineweather.core.utils.getResult
import com.iti.fineweather.core.utils.navigate
import com.iti.fineweather.features.common.views.ClearStatusBar
import com.iti.fineweather.features.home.views.HomeScreen
import com.iti.fineweather.features.map.models.MapPlaceResult
import com.iti.fineweather.features.map.views.MapScreen
import com.iti.fineweather.features.settings.models.UserPreferences.Language
import com.iti.fineweather.features.settings.utils.toLocale
import com.iti.fineweather.features.settings.utils.toLocalizedName
import com.iti.fineweather.features.settings.viewmodels.SettingsViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

object SplashScreen : Screen<SplashScreen.SplashRoute> {

    override val routeInfo: SplashRoute = SplashRoute

    object SplashRoute : RouteInfo {
        override val path: String = "splash"
        override val screen: @Composable () -> Unit = @Composable {
            SplashScreen()
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@VisibleForTesting
fun SplashScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val configurations = LocalConfiguration.current
    val navController = LocalNavigation.navController
    val permissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    ) { permissions ->
        @SuppressLint("MissingPermission")
        if (permissions.any { p -> p.value }) {
            settingsViewModel.updateGpsLocation()
        }
    }
    val userPreferencesState by settingsViewModel.uiState.collectAsState()
    if (userPreferencesState is UiState.Loaded) {
        val userPreferences = userPreferencesState.data!!
        when {
            !userPreferences.hasLanguage() -> {
                MissingLanguageDialog(settingsViewModel = settingsViewModel)
            }
            !userPreferences.hasLocation() -> {
                MissingLocation(settingsViewModel = settingsViewModel, permissions = permissions)
            }
            else -> {
                LaunchedEffect(key1 = true) {
                    configurations.setLocale(userPreferences.language.toLocale())
                    if (settingsViewModel.isLocationUpdateNeeded) {
                        permissions.launchMultiplePermissionRequest()
                    }
                    navController.navigate(HomeScreen.routeInfo.toNavRequest())
                }
            }
        }
    }

    ClearStatusBar {
        CompositionScaffoldProvider {
            Scaffold(
                scaffoldState = LocalScaffold.current,
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) { }
            }
        }
    }
}

@Composable
@VisibleForTesting
fun MissingLanguageDialog(
    settingsViewModel: SettingsViewModel
) {
    val configs = LocalConfiguration.current
    var language by remember { mutableStateOf<Language?>(null) }

    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "Select your language") }, // TODO: localize
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(Language.ARABIC, Language.ENGLISH).forEach { item ->
                   Row(
                       verticalAlignment = Alignment.CenterVertically,
                   ) {
                       RadioButton(
                           selected = language == item,
                           onClick = {
                               language = item
                               configs.setLocale(item.toLocale())
                           },
                           colors = RadioButtonDefaults.colors(MaterialTheme.colors.primary),
                       )
                       Text(
                           text = item.toLocalizedName(),
                           modifier = Modifier.padding(start = LocalAppTheme.spaces.small)
                       )
                   }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = language != null,
                onClick = {
                    language?.let { language -> settingsViewModel.updateLanguage(language) }
                },
            ) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@VisibleForTesting
fun MissingLocation(
    settingsViewModel: SettingsViewModel,
    permissions: MultiplePermissionsState
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentStackEntry = LocalNavigation.backStackEntry;
    val navController = LocalNavigation.navController
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "Location") }, // TODO: localize
        text = {
            Text(text = "How you you like to select your location ?")
        },
        confirmButton = {
            Row {
                Button(
                    onClick = {
                        navController.navigate(MapScreen.routeInfo.toNavRequest())
                        lifecycleOwner.lifecycleScope.launch {
                            val locationResult = currentStackEntry.getResult<MapPlaceResult>(MapScreen.RESULT_KEY)
                            if (locationResult != null) {
                                Timber.d("GOT $locationResult")
                                settingsViewModel.updateMapLocation(locationResult)
                            }
                        }
                    },
                ) {
                    Text(text = "Map") // TODO: localize
                }
                Spacer(modifier = Modifier.width(LocalAppTheme.spaces.medium))
                Button(
                    onClick = {
                        permissions.launchMultiplePermissionRequest()
                    },
                ) {
                    Text(text = "GPS") // TODO: localize
                }
            }
        },
    )
}
