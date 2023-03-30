package com.iti.fineweather.features.map.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.iti.fineweather.core.helpers.Resource
import com.iti.fineweather.core.helpers.UiState
import com.iti.fineweather.features.map.models.MapPlace
import com.iti.fineweather.features.map.repositories.MapPlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapPlaceViewModel @Inject constructor(private val placesRepository: MapPlacesRepository) : ViewModel() {

    private var job: Job? = null
    private val _uiState = MutableStateFlow<UiState<MapPlace>>(UiState.Initial())
    val uiState = _uiState.asStateFlow()

    fun getPlace(place: AutocompletePrediction) {
        job?.cancel()
        job = viewModelScope.launch {
            _uiState.value = _uiState.value.toLoading(preserveData = false)
            val result = placesRepository.getPlace(place)
            _uiState.value = when (result) {
                is Resource.Success -> _uiState.value.toLoaded(result.data)
                is Resource.Error -> _uiState.value.toError(result.error, preserveData = false)
            }
        }
    }

    fun getPlace(location: LatLng) {
        job?.cancel()
        job = viewModelScope.launch {
            _uiState.value = _uiState.value.toLoading(preserveData = false)
            val result = placesRepository.getPlace(location)
            _uiState.value = when (result) {
                is Resource.Success -> _uiState.value.toLoaded(result.data)
                is Resource.Error -> _uiState.value.toError(result.error, preserveData = false)
            }
        }
    }
}
