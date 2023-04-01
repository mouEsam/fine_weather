package com.iti.fineweather.features.common.helpers

import com.iti.fineweather.features.settings.models.UserPreferences

val UserPreferences.Language.languageCode: String?
    get() {
        return when (this) {
            UserPreferences.Language.ARABIC -> "ar"
            UserPreferences.Language.ENGLISH -> "en"
            UserPreferences.Language.UNRECOGNIZED -> null
        }
    }