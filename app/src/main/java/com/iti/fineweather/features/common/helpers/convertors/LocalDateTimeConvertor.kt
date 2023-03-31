package com.iti.fineweather.features.common.helpers.convertors

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object LocalDateTimeConvertor {
    @TypeConverter
    fun toDateTime(dateTimeLong: Long?): LocalDateTime? {
        return if (dateTimeLong == null) {
            null
        } else {
            Instant.ofEpochMilli(dateTimeLong).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }
    }

    @TypeConverter
    fun fromDateTime(dateTime: LocalDateTime?): Long? {
        return dateTime?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }
}