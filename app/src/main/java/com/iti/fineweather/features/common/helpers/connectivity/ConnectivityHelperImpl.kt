package com.iti.fineweather.features.common.helpers.connectivity

import android.content.Context
import com.iti.fineweather.core.utils.ConnectionState
import com.iti.fineweather.core.utils.observeConnectivityAsFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConnectivityHelperImpl @Inject constructor(
    @ApplicationContext private val context: Context,
): ConnectivityHelper {
    override val connectivityStateFlow: Flow<ConnectionState> = context.observeConnectivityAsFlow()
}