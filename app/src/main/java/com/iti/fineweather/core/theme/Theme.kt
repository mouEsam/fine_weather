package com.iti.fineweather.core.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat

@Composable
fun FineWeatherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val localAppColors: AppColorScheme by remember {
        derivedStateOf {
            if (darkTheme) {
                AppDarkColorScheme
            } else {
                AppLightColorScheme
            }
        }
    }

    val materialColorScheme by remember {
        derivedStateOf {
            localAppColors.mapToMaterialColorSchema()
        }
    }

    val materialTypography by remember {
        derivedStateOf {
            AppTypography.mapToMaterialTypography()
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.also { window ->
                window.statusBarColor = materialColorScheme.primary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalAppColor provides localAppColors,
        LocalAppTypography provides AppTypography,
        LocalAppShape provides AppShape,
        LocalAppSpace provides AppSpace
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = materialTypography,
            content = content
        )
    }
}