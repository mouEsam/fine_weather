package com.iti.fineweather.core.utils

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.os.LocaleListCompat
import androidx.core.text.layoutDirection
import timber.log.Timber
import java.util.*
import android.util.LayoutDirection as legacyDirection

class LocaleController internal constructor(
    val changeLocale: (locale: Locale) -> Unit,
)

val LocalLocaleController: ProvidableCompositionLocal<LocaleController> =
    staticCompositionLocalOf { error("not provided") }

private fun Context.updateLocale(locale: Locale) {
    val configs = resources.configuration
    configs.setLocale(locale)
    configs.setLayoutDirection(locale)
    Locale.setDefault(locale)
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(locale.toLanguageTag()))
    @Suppress("DEPRECATION")
    resources.updateConfiguration(configs, resources.displayMetrics)
}

@Composable
fun AppLayoutDirection(
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    var locale by remember { mutableStateOf(configuration.locales[0]) }


    val localeController = LocaleController(
        changeLocale = { it ->
            context.updateLocale(it)
            configuration.setLocale(it)
            configuration.setLayoutDirection(it)
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