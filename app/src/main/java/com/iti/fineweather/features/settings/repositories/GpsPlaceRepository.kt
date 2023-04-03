package com.iti.fineweather.features.settings.repositories

import android.location.Geocoder
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.iti.fineweather.core.helpers.Resource
import com.iti.fineweather.features.common.utils.getAddress
import com.iti.fineweather.features.map.models.MapPlaceResult
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@ActivityRetainedScoped
class GpsPlaceRepository @Inject constructor(
    private val fusedLocation: FusedLocationProviderClient,
    private val geocoder: Geocoder,
) {

    @RequiresPermission(
        anyOf = [
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
        ]
    )
    suspend fun getLocation(): Resource<MapPlaceResult> = suspendCancellableCoroutine { continuation ->
        val cancellationTokenSource = CancellationTokenSource()
        fusedLocation.getCurrentLocation(
            CurrentLocationRequest
                .Builder()
                .build(),
            cancellationTokenSource.token,
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val location = task.result
                val latlng = LatLng(location.latitude, location.longitude)
                geocoder.getAddress(location.latitude, location.longitude) { address, _ ->
                    val name = address?.featureName ?: ""
                    continuation.resume(Resource.Success.Remote(MapPlaceResult(latlng, name)))
                }
            } else if (task.isComplete) {
                continuation.resumeWithException(task.exception!!)
            }
        }
        continuation.invokeOnCancellation { cancellationTokenSource.cancel() }
    }

}
