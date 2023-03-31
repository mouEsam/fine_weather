package com.iti.fineweather.features.alerts.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

enum class RepetitionType {
    SINGLE,
    DAILY,
}

@Entity
data class WeatherAlert(
    @PrimaryKey
    var id: String,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var deleteAt: LocalDateTime? = null,
    var alarmEnabled: Boolean,
    var repetitionType: RepetitionType,
    var time: LocalTime,
    var startDate: LocalDate,
    var endDate: LocalDate? = null,
)
