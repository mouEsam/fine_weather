package com.iti.fineweather.features.common.repositories

import androidx.datastore.core.DataStore
import com.iti.fineweather.core.helpers.Resource
import com.iti.fineweather.features.settings.models.UserPreferences
import com.iti.fineweather.features.settings.models.UserPreferences.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val userPreferencesStore: DataStore<UserPreferences>,
) {
    val userPreferencesFlow: Flow<Resource<UserPreferences>> = userPreferencesStore.data
        .map {  prefs ->
            Resource.Success.Local(prefs) as Resource<UserPreferences>
        }
        .catch { exception ->
            when (exception) {
                is IOException -> {
                    Timber.e(exception, "Error reading user preferences.")
                    emit(Resource.Success.Fallback(UserPreferences.getDefaultInstance()))
                }
                is Exception ->  emit(Resource.Error(exception))
                else -> throw exception
            }
        }

    suspend fun updateLocationType(locationType: LocationType) {
        userPreferencesStore.updateData { preferences ->
            preferences.toBuilder()
                .setLocationType(locationType)
                .build()
        }
    }

    suspend fun updateLocation(location: MapPlace) {
        userPreferencesStore.updateData { preferences ->
            preferences.toBuilder()
                .setLocation(location)
                .build()
        }
    }

    suspend fun updateTemperatureUnit(temperatureUnit: TemperatureUnit) {
        userPreferencesStore.updateData { preferences ->
            preferences.toBuilder()
                .setTemperatureUnit(temperatureUnit)
                .build()
        }
    }

    suspend fun updateWindSpeedUnit(windSpeedUnit: WindSpeedUnit) {
        userPreferencesStore.updateData { preferences ->
            preferences.toBuilder()
                .setWindSpeedUnit(windSpeedUnit)
                .build()
        }
    }

    suspend fun updateLanguage(language: Language) {
        userPreferencesStore.updateData { preferences ->
            preferences.toBuilder()
                .setLanguage(language)
                .build()
        }
    }
}
