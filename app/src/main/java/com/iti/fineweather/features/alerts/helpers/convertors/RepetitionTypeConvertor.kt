package com.iti.fineweather.features.alerts.helpers.convertors

import androidx.room.TypeConverter
import com.iti.fineweather.features.alerts.entities.RepetitionType

object RepetitionTypeConvertor {
    @TypeConverter
    fun toRepetitionType(name: String): RepetitionType {
        return RepetitionType.valueOf(name)
    }

    @TypeConverter
    fun fromRepetitionType(repetitionType: RepetitionType): String {
        return repetitionType.name
    }
}