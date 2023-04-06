package com.iti.fineweather.features.alerts.entities

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iti.fineweather.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

enum class RepetitionType {
    SINGLE,
    DAILY;

    @Composable
    fun toLocalizedName(): String {
        return when (this) {
            // TODO: localize
            RepetitionType.SINGLE -> stringResource(R.string.alerts_repetition_type_once)
            RepetitionType.DAILY -> stringResource(R.string.alerts_repetition_type_daily)
        }
    }
}

@Entity
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
