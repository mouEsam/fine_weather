package com.iti.fineweather.features.common.utils

import kotlin.math.pow
import kotlin.math.roundToInt

fun Float.round(decimalPlaces: Int): Float {
    val modifier = (10f).pow(decimalPlaces)
    return (this * modifier).roundToInt() / modifier
}
