package com.iti.fineweather.features.settings.views

import android.annotation.SuppressLint
import androidx.annotation.VisibleForTesting
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.iti.fineweather.core.helpers.UiState
import com.iti.fineweather.features.settings.viewmodels.SettingsViewModel
import timber.log.Timber


@OptIn(ExperimentalPermissionsApi::class)
@Composable
@VisibleForTesting
fun SettingsPage(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val permissions = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
        )
    ) { permissions ->
        @SuppressLint("MissingPermission")
        if (permissions.any { p -> p.value }) {
            settingsViewModel.updateGpsLocation()
        } else {
            // TODO: on failure
        }
    }

    val opState by settingsViewModel.operationState.collectAsState()
    LaunchedEffect(key1 = opState) {
        Timber.d(opState.toString())
        when (opState) {
            is UiState.Error -> {
                // TODO: handle error
            }
            else -> {}
        }
    }

    val settingsUiState by settingsViewModel.uiState.collectAsState()
    LaunchedEffect(key1 = settingsUiState) {
        Timber.d(settingsUiState.toString())
    }

    Button(onClick = {
        permissions.launchMultiplePermissionRequest()
    }) {
        Text("Get location")
    }
}

