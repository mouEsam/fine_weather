package com.iti.fineweather.features.common.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.iti.fineweather.core.helpers.LocalScaffold
import com.iti.fineweather.core.helpers.UiState
import com.iti.fineweather.core.utils.error

@Composable
fun <T> showErrorSnackbar(uiState: UiState<T>) {
    val error = uiState.error?.error
    val snackbarState = LocalScaffold.snackbarHost

    LaunchedEffect(key1 = uiState) {
        error?.let { error ->
            snackbarState.showSnackbar(error)
        }
    }
}
