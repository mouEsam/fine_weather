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
    val id: UUID = UUID.randomUUID(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null,
    val alarmEnabled: Boolean,
    @ColumnInfo(name = "repetition_type")
    val repetitionType: RepetitionType,
    @ColumnInfo(name = "time")
    val time: LocalTime,
    @ColumnInfo(name = "start_date")
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val exhausted: Boolean = false,
)
