package com.iti.fineweather.core.utils

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.*

fun Configuration.updateLocale(
    context: Context,
    localeController: LocaleController,
    locale: Locale,
) {
    context.updateLocale(locale)
    setLocale(locale)
    setLayoutDirection(locale)
    localeController.changeLocale(locale)
}

private fun Context.updateLocale(locale: Locale) {
    val configs = resources.configuration
    configs.setLocale(locale)
    configs.setLayoutDirection(locale)
    Locale.setDefault(locale)
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(locale.toLanguageTag()))
    @Suppress("DEPRECATION")
    resources.updateConfiguration(configs, resources.displayMetrics)
}
