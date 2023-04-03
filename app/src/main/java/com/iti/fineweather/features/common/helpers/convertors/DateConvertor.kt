package com.iti.fineweather.features.common.helpers.convertors

import androidx.room.TypeConverter
import java.util.*

object DateConvertor {
    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return if (dateLong == null) null else Date(dateLong)
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
}