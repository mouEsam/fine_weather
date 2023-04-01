package com.iti.fineweather.features.alerts.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

enum class RepetitionType {
    SINGLE,
    DAILY,
}

@Entity(
    indices = [Index(value = ["repetition_type", "time", "start_date"], unique = true)]
)
data class UserWeatherAlert(
    @PrimaryKey
    var id: UUID = UUID.randomUUID(),
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var deleteAt: LocalDateTime? = null,
    var alarmEnabled: Boolean,
    @ColumnInfo(name = "repetition_type")
    var repetitionType: RepetitionType,
    @ColumnInfo(name = "time")
    var time: LocalTime,
    @ColumnInfo(name = "start_date")
    var startDate: LocalDate,
    var endDate: LocalDate? = null,
)
