package com.iti.fineweather.features.common.helpers.connectivity

import com.iti.fineweather.core.utils.ConnectionState
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

interface ConnectivityHelper {
    val connectivityStateFlow: Flow<ConnectionState>
}