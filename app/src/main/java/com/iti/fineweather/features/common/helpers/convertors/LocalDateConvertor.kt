package com.iti.fineweather.features.common.helpers.convertors

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object LocalDateConvertor {
    @TypeConverter
    fun toDate(dateLong: Long?): LocalDate? {
        return if (dateLong == null) {
            null
        } else {
            Instant.ofEpochMilli(dateLong).atZone(ZoneId.systemDefault()).toLocalDate()
        }
    }

    @TypeConverter
    fun fromDate(date: LocalDate?): Long? {
        return date?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }
}