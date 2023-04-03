package com.iti.fineweather.features.common.utils

import android.location.Geocoder
import android.os.Build

@Suppress("DEPRECATION")
fun Geocoder.getAddress(
    latitude: Double,
    longitude: Double,
    onResult: (android.location.Address?, Exception?) -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getFromLocation(latitude, longitude, 1) { onResult(it.firstOrNull(), null) }
        return
    }
    try {
        onResult(getFromLocation(latitude, longitude, 1)?.firstOrNull(), null)
    } catch(e: Exception) {
        onResult(null, e)
    }
}