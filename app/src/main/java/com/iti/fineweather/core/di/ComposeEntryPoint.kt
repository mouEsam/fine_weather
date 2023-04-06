package com.iti.fineweather.core.di

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.iakovlev.timeshape.TimeZoneEngine

private lateinit var composeEntryPoint: ComposeEntryPoint

@Composable
fun requireComposeEntryPoint(): ComposeEntryPoint {
    if (!::composeEntryPoint.isInitialized) {
        composeEntryPoint = EntryPoints.get(
                LocalContext.current.applicationContext,
                ComposeEntryPoint::class.java,
            )
    }
    return composeEntryPoint
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ComposeEntryPoint {
    val timeZoneEngine: TimeZoneEngine
}
