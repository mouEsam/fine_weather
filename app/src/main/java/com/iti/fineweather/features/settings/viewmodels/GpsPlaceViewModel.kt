package com.iti.fineweather.features.settings.viewmodels

import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.fineweather.core.helpers.Resource
import com.iti.fineweather.core.helpers.UiState
import com.iti.fineweather.features.map.models.MapPlaceResult
import com.iti.fineweather.features.settings.repositories.GpsPlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GpsPlaceViewModel @Inject constructor(
    private val gpsPlaceRepository: GpsPlaceRepository
) : ViewModel() {

    private var job: Job? = null
    private val _uiState = MutableStateFlow<UiState<MapPlaceResult>>(UiState.Initial())
    val uiState = _uiState.asStateFlow()

    @RequiresPermission(
        anyOf = [
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
        ]
    )
    fun getLocation() {
        job?.cancel()
        job = viewModelScope.launch {
            _uiState.value = _uiState.value.toLoading(preserveData = false)
            val result = gpsPlaceRepository.getLocation()
            _uiState.value = when (result) {
                is Resource.Success -> _uiState.value.toLoaded(result.data)
                is Resource.Error -> _uiState.value.toError(result.error, preserveData = false)
            }
        }
    }
}
