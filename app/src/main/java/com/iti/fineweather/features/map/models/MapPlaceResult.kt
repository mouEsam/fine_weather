package com.iti.fineweather.features.map.models

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.iti.fineweather.features.settings.models.UserPreferences
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapPlaceResult(val location: LatLng, val name: String): Parcelable {

    // TODO: extract mapper
    fun toMapPlace(): UserPreferences.MapPlace {
        return UserPreferences.MapPlace.newBuilder().setName(name)
            .setLocation(
                UserPreferences.MapPlace.Location.newBuilder()
                    .setLatitude(location.latitude)
                    .setLongitude(location.longitude)
                    .build()
            ).build()
    }
}
