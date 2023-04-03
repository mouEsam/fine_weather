package com.iti.fineweather.features.common.repositories

import androidx.datastore.core.DataStore
import com.iti.fineweather.core.di.IODispatcher
import com.iti.fineweather.core.helpers.Resource
import com.iti.fineweather.features.settings.models.UserPreferences
import com.iti.fineweather.features.settings.models.UserPreferences.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val userPreferencesStore: DataStore<UserPreferences>,
    @IODispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    val userPreferencesFlow: Flow<Resource<UserPreferences>> = userPreferencesStore.data
        .map<_, Resource<UserPreferences>> {  prefs ->
            Resource.Success.Local(prefs)
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
        }.flowOn(dispatcher)

    suspend fun updateLocationType(locationType: LocationType) {
        withContext(dispatcher) {
            userPreferencesStore.updateData { preferences ->
                preferences.toBuilder()
                    .setLocationType(locationType)
                    .build()
            }
        }
    }

    suspend fun updateLocation(location: MapPlace) {
        withContext(dispatcher) {
            userPreferencesStore.updateData { preferences ->
                preferences.toBuilder()
                    .setLocation(location)
                    .build()
            }
        }
    }

    suspend fun updateGpsLocation(location: MapPlace) {
        withContext(dispatcher) {
            userPreferencesStore.updateData { preferences ->
                preferences.toBuilder()
                    .setLocation(location)
                    .setLocationType(LocationType.GPS)
                    .build()
            }
        }
    }

    suspend fun updateMapLocation(location: MapPlace) {
        withContext(dispatcher) {
            userPreferencesStore.updateData { preferences ->
                preferences.toBuilder()
                    .setLocation(location)
                    .setLocationType(LocationType.MAP)
                    .build()
            }
        }
    }

    suspend fun updateTemperatureUnit(temperatureUnit: TemperatureUnit) {
        withContext(dispatcher) {
            userPreferencesStore.updateData { preferences ->
                preferences.toBuilder()
                    .setTemperatureUnit(temperatureUnit)
                    .build()
            }
        }
    }

    suspend fun updateWindSpeedUnit(windSpeedUnit: WindSpeedUnit) {
        withContext(dispatcher) {
            userPreferencesStore.updateData { preferences ->
                preferences.toBuilder()
                    .setWindSpeedUnit(windSpeedUnit)
                    .build()
            }
        }
    }

    suspend fun updateLanguage(language: Language) {
        withContext(dispatcher) {
            userPreferencesStore.updateData { preferences ->
                preferences.toBuilder()
                    .setLanguage(language)
                    .build()
            }
        }
    }
}
