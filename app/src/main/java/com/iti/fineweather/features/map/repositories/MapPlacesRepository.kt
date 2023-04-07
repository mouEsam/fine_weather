package com.iti.fineweather.features.map.repositories

import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.iti.fineweather.R
import com.iti.fineweather.core.helpers.InvalidStateException
import com.iti.fineweather.core.helpers.Resource
import com.iti.fineweather.features.common.utils.getAddress
import com.iti.fineweather.features.map.models.MapPlaceResult
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@ActivityRetainedScoped
class MapPlacesRepository @Inject constructor(
    private val placesClient: PlacesClient,
    private val geocoder: Geocoder,
){

    suspend fun getPredictions(input: String): Resource<List<AutocompletePrediction>> = get(
        taskCreator = { token ->
            placesClient.findAutocompletePredictions(
                FindAutocompletePredictionsRequest.builder()
                    .setCancellationToken(token)
                    .setQuery(input)
                    .build()
            )
        },
        resultMapper = { it.autocompletePredictions }
    )

    suspend fun getPlace(place: AutocompletePrediction): Resource<MapPlaceResult> = get(
        taskCreator = { token ->
            placesClient.fetchPlace(
                FetchPlaceRequest.builder(place.placeId, listOf(Place.Field.LAT_LNG, Place.Field.NAME))
                    .setCancellationToken(token)
                    .build()
            )
        },
        resultMapper = {
            MapPlaceResult(
                location = it.place.latLng!!,
                name = it.place.name!!,
                city = it.place.name!!,
            )
        }
    )

    suspend fun getPlace(location: LatLng): Resource<MapPlaceResult> = suspendCoroutine { continuation ->
        geocoder.getAddress(location.latitude, location.longitude) { address, exception ->
            if (exception != null) {
                continuation.resume(Resource.Error(exception))
            } else if (address != null) {
                continuation.resume(Resource.Success.Remote(
                    MapPlaceResult(
                        location = location,
                        name = address.featureName,
                        city = address.subAdminArea ?: address.countryName ?: address.featureName,
                    )
                ))
            } else {
                continuation.resume(Resource.Error(InvalidStateException(R.string.error_place_not_found)))
            }
        }
    }

    private suspend fun <R, T> get(
        taskCreator: (CancellationToken) -> Task<T>,
        resultMapper: (T) -> R
    ): Resource<R> = suspendCancellableCoroutine { continuation ->
        val cancellationTokenSource = CancellationTokenSource()
        taskCreator(cancellationTokenSource.token).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(Resource.Success.Remote(resultMapper(task.result!!)))
            } else if (!task.isCanceled) {
                continuation.resume(Resource.Error(task.exception!!))
            }
        }
        continuation.invokeOnCancellation { cancellationTokenSource.cancel() }
    }
}
