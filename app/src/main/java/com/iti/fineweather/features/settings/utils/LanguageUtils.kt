package com.iti.fineweather.features.settings.utils

import androidx.compose.runtime.Composable
import com.iti.fineweather.features.settings.models.UserPreferences
import java.util.*

fun UserPreferences.Language.toLocale(): Locale {
    return when (this) {
        UserPreferences.Language.ARABIC -> Locale("ar")
        UserPreferences.Language.ENGLISH -> Locale.ENGLISH
        UserPreferences.Language.UNRECOGNIZED -> Locale.ENGLISH
    }
}

@Composable
fun UserPreferences.Language.toLocalizedName(): String {
    return when (this) {
        // TODO: localize
        UserPreferences.Language.ARABIC -> "Arabic"
        UserPreferences.Language.ENGLISH -> "English"
        UserPreferences.Language.UNRECOGNIZED -> "Arabic"
    }
}