package com.iti.fineweather.core.utils

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.text.layoutDirection
import timber.log.Timber
import java.util.*
import android.util.LayoutDirection as legacyDirection

class LocaleController internal constructor(
    val changeLocale: (locale: Locale) -> Unit,
)

val LocalLocaleController: ProvidableCompositionLocal<LocaleController> =
    staticCompositionLocalOf { error("not provided") }

@Composable
fun AppLayoutDirection(
    content: @Composable () -> Unit,
) {
    val configuration = LocalConfiguration.current
    var locale by remember { mutableStateOf(configuration.locales[0]) }


    val localeController = LocaleController(
        changeLocale = {
            locale = it
        }
    )

    CompositionLocalProvider(
        LocalLocaleController provides localeController,
        LocalLayoutDirection provides if (locale.layoutDirection == legacyDirection.RTL)
            LayoutDirection.Rtl else
            LayoutDirection.Ltr,
    ) {
        Timber.d(configuration.locales[0].toLanguageTag())
        content()
    }
}