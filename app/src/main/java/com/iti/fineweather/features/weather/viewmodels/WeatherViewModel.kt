package com.iti.fineweather.features.weather.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.fineweather.R
import com.iti.fineweather.core.di.CPUDispatcher
import com.iti.fineweather.core.helpers.Resource
import com.iti.fineweather.core.helpers.UiState
import com.iti.fineweather.features.common.repositories.UserPreferencesRepository
import com.iti.fineweather.features.settings.models.UserPreferences
import com.iti.fineweather.features.settings.utils.toLocale
import com.iti.fineweather.features.weather.models.RemoteWeatherResponse
import com.iti.fineweather.features.weather.models.WeatherLocation
import com.iti.fineweather.features.weather.models.WeatherUnitData
import com.iti.fineweather.features.weather.models.WeatherViewData
import com.iti.fineweather.features.weather.repositories.WeatherRepository
import com.iti.fineweather.features.weather.utils.toWeatherLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    @CPUDispatcher private val computeDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private var mutex: Mutex = Mutex()
    private var job: Job? = null
    private var location: WeatherLocation? = null
    private var weatherDataResponse: RemoteWeatherResponse? = null
    private val _uiState = MutableStateFlow<UiState<WeatherViewData>>(UiState.Initial())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.map { it.data }
                .filterNotNull()
                .distinctUntilChangedBy { preferences -> preferences.language }.collectLatest { _ ->
                    location?.let { location ->
                        getWeatherData(location)
                    }
                }
        }
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.map { it.data }
                .filterNotNull()
                .distinctUntilChangedBy { preferences ->
                    Pair(preferences.temperatureUnit, preferences.windSpeedUnit)
                }.collectLatest { preferences ->
                    weatherDataResponse?.let { weatherDataResponse ->
                        withContext(computeDispatcher) {
                            setData(
                                location = location!!,
                                result = Resource.Success.Remote(weatherDataResponse),
                                preferences = preferences
                            )
                        }
                    }
                }
        }
    }


    fun getWeatherData(weatherLocation: WeatherLocation? = null) {
        job?.cancel()
        job = viewModelScope.launch {
            _uiState.value = _uiState.value.toLoading(preserveData = false)
            val preferences = userPreferencesRepository
                .userPreferencesFlow
                .map { it.data }
                .filterNotNull()
                .first()
            val location = weatherLocation ?: preferences.location.toWeatherLocation()
            val result = weatherRepository.getWeatherData(
                latitude = location.latitude,
                longitude = location.longitude,
                locale = preferences.language.toLocale()
            )
            setData(location, result, preferences)
        }
    }

    @OptIn(InternalCoroutinesApi::class)
    private suspend fun setData(location: WeatherLocation,
                                result: Resource<RemoteWeatherResponse>,
                                preferences: UserPreferences) {
        withContext(computeDispatcher) {
            synchronized(this@WeatherViewModel) {
                _uiState.value = when (result) {
                    is Resource.Success -> {
                        weatherDataResponse = result.data
                        this@WeatherViewModel.location = location
                        val weatherViewData = mapWeatherDataToViewData(location, result.data, preferences)
                        _uiState.value.toLoaded(weatherViewData)
                    }
                    is Resource.Error -> {  _uiState.value.toError(result.error, preserveData = false) }
                }
            }
        }
    }

    private fun mapWeatherDataToViewData(location: WeatherLocation,
                                         data: RemoteWeatherResponse,
                                         preferences: UserPreferences): WeatherViewData {
        // TODO: convert units
        return WeatherViewData(
            location = WeatherLocation(name = location.name, latitude = data.lat, longitude = data.lon),
            units = WeatherUnitData(
                // TODO: change
                temperature = R.string.placeholder,
                pressure = R.string.placeholder,
                speed = R.string.placeholder,
                length = R.string.placeholder,
                accumulation = R.string.placeholder,
            ),
            temp = data.currentWeather.temp,
            pressure = data.currentWeather.pressure,
            weatherState = data.currentWeather.weather.firstOrNull(),
        )
    }
}
