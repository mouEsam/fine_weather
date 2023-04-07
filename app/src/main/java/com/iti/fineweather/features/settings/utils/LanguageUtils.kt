package com.iti.fineweather.features.settings.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.iti.fineweather.R
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
        UserPreferences.Language.ARABIC -> stringResource(R.string.settings_language_ar)
        UserPreferences.Language.ENGLISH,
        UserPreferences.Language.UNRECOGNIZED -> stringResource(R.string.settings_language_en)
    }
}

@Composable
fun UserPreferences.LocationType.toLocalizedName(): String {
    return when (this) {
        UserPreferences.LocationType.MAP -> stringResource(R.string.settings_location_source_map)
        UserPreferences.LocationType.GPS,
        UserPreferences.LocationType.UNRECOGNIZED -> stringResource(R.string.settings_location_source_gps)
    }
}

@Composable
fun UserPreferences.TemperatureUnit.toLocalizedName(): String {
    return when (this) {
        UserPreferences.TemperatureUnit.CELSIUS,
        UserPreferences.TemperatureUnit.UNRECOGNIZED -> stringResource(R.string.settings_temperature_unit_celsius)
        UserPreferences.TemperatureUnit.KELVIN -> stringResource(R.string.settings_temperature_unit_kelvin)
        UserPreferences.TemperatureUnit.FAHRENHEIT -> stringResource(R.string.settings_temperature_unit_fahrenheit)
    }
}

@Composable
fun UserPreferences.WindSpeedUnit.toLocalizedName(): String {
    return when (this) {
        UserPreferences.WindSpeedUnit.METER_SEC,
        UserPreferences.WindSpeedUnit.UNRECOGNIZED -> stringResource(R.string.settings_wind_speed_unit_meter_s)
        UserPreferences.WindSpeedUnit.MILES_HOUR -> stringResource(R.string.settings_wind_speed_unit_miles_hr)
    }
}