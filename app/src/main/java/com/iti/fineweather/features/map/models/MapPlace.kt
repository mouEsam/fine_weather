package com.iti.fineweather.features.map.models

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapPlace(val location: LatLng, val name: String): Parcelable
