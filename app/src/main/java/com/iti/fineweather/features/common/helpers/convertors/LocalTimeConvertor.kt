package com.iti.fineweather.features.common.helpers.convertors

import androidx.room.TypeConverter
import java.time.LocalTime

object LocalTimeConvertor {
    @TypeConverter
    fun toTime(timeSeconds: Int?): LocalTime? {
        return if (timeSeconds == null) {
            null
        } else {
            LocalTime.ofSecondOfDay(timeSeconds.toLong())
        }
    }

    @TypeConverter
    fun fromTime(time: LocalTime?): Int? {
        return time?.toSecondOfDay()
    }
}