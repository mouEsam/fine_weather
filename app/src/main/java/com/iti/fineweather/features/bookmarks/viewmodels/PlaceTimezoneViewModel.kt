package com.iti.fineweather.features.bookmarks.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.iti.fineweather.core.di.IODispatcher
import com.iti.fineweather.core.helpers.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import net.iakovlev.timeshape.TimeZoneEngine
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class PlaceTimezoneViewModel @Inject constructor(
    private val timezoneEngineProvider: Provider<TimeZoneEngine>,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private lateinit var location: LatLng

    fun setLocation(latLng: LatLng) {
        location = latLng
    }

    val timezoneState: StateFlow<UiState<ZoneId>> by lazy {
        channelFlow<UiState<ZoneId>> {
            withContext(dispatcher) {
                try {
                    val engine = timezoneEngineProvider.get()
                    val timezone = engine.query(location.latitude, location.longitude).orElse(ZoneId.systemDefault())
                    send(UiState.Loaded(timezone))
                } catch (e: Exception) {
                    send(UiState.Error(e))
                }
            }
        }.stateIn(viewModelScope, started = SharingStarted.Lazily, initialValue = UiState.Loading())
    }

}
